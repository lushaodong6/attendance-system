package com.example.attendance.service;

import com.example.attendance.dto.ImportError;
import com.example.attendance.dto.UploadResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.util.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExcelUploadService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private static final Set<String> VALID_STATUS = Set.of("出勤", "迟到", "早退", "缺勤");

    /**
     * 处理Excel文件上传，包含大小验证、格式验证、数据验证
     */
    @Transactional
    public UploadResult processExcel(MultipartFile file) {
        UploadResult result = new UploadResult();

        // ========== 任务1: 文件验证 ==========
        // 1.1 文件大小验证 (前端最大10MB，这里再做一次)
        if (file.getSize() > 10 * 1024 * 1024) {
            result.setTotalRecords(0);
            result.setSuccessCount(0);
            result.setFailCount(1);
            result.getErrors().add(new ImportError(0, "", "file", "文件大小超过10MB限制"));
            return result;
        }

        // 1.2 文件格式验证 (仅支持 .xlsx)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            result.getErrors().add(new ImportError(0, "", "file", "仅支持 .xlsx 或 .xls 格式的Excel文件"));
            return result;
        }

        // 内容类型验证
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                && !contentType.equals("application/vnd.ms-excel")) {
            result.getErrors().add(new ImportError(0, "", "file", "无效的Excel文件类型"));
            return result;
        }

        // ========== 解析Excel ==========
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows() - 1; // 减去标题行
            result.setTotalRecords(totalRows);

            int success = 0;
            List<ImportError> errors = new ArrayList<>();

            // 遍历每一行 (从第2行开始，索引1)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // 获取单元格值
                String studentId = getCellStringValue(row.getCell(0));
                String name = getCellStringValue(row.getCell(1));
                String className = getCellStringValue(row.getCell(2));
                String dateStr = getCellStringValue(row.getCell(3));
                String status = getCellStringValue(row.getCell(4));

                // 数据验证
                List<String> rowErrors = new ArrayList<>();

                if (studentId == null || studentId.trim().isEmpty()) rowErrors.add("学号不能为空");
                if (name == null || name.trim().isEmpty()) rowErrors.add("姓名不能为空");
                if (className == null || className.trim().isEmpty()) rowErrors.add("班级不能为空");

                LocalDate date = null;
                if (dateStr == null || dateStr.trim().isEmpty()) {
                    rowErrors.add("日期不能为空");
                } else {
                    date = DateUtil.parseDate(dateStr);
                    if (date == null) rowErrors.add("日期格式无效，支持格式: yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy 等");
                }

                if (status == null || status.trim().isEmpty()) {
                    rowErrors.add("状态不能为空");
                } else if (!VALID_STATUS.contains(status)) {
                    rowErrors.add("状态必须是以下之一: " + String.join(", ", VALID_STATUS));
                }

                if (!rowErrors.isEmpty()) {
                    for (String errMsg : rowErrors) {
                        errors.add(new ImportError(rowIndex + 1, studentId, "", errMsg));
                    }
                    continue;
                }

                // 保存或更新学生
                Student student = studentRepository.findByStudentId(studentId).orElse(null);
                if (student == null) {
                    student = new Student(studentId, name, className);
                    student = studentRepository.save(student);
                } else {
                    // 如果学生已存在，更新姓名和班级
                    student.setName(name);
                    student.setClassName(className);
                    student = studentRepository.save(student);
                }

                // 保存出勤记录 (防止重复)
                Optional<Attendance> existing = attendanceRepository.findByStudentAndDate(student, date);
                if (existing.isPresent()) {
                    Attendance att = existing.get();
                    att.setStatus(status);
                    attendanceRepository.save(att);
                } else {
                    Attendance attendance = new Attendance(student, date, status);
                    attendanceRepository.save(attendance);
                }
                success++;
            }

            result.setSuccessCount(success);
            result.setFailCount(totalRows - success);
            result.setErrors(errors);

        } catch (Exception e) {
            result.setTotalRecords(0);
            result.setSuccessCount(0);
            result.setFailCount(1);
            result.getErrors().add(new ImportError(0, "", "file", "解析失败: " + e.getMessage()));
        }

        return result;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 如果是数字，尝试转换为字符串 (学号可能是数字)
                return String.valueOf((long) cell.getNumericCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}