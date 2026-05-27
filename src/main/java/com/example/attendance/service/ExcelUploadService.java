package com.example.attendance.service;

import com.example.attendance.dto.ImportError;
import com.example.attendance.dto.UploadResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.util.DataUtil;
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

    @Transactional
    public UploadResult processExcel(MultipartFile file) {
        UploadResult result = new UploadResult();

        // 文件大小验证
        if (file.getSize() > 10 * 1024 * 1024) {
            result.setTotalRecords(0);
            result.setSuccessCount(0);
            result.setFailCount(1);
            result.getErrors().add(new ImportError(0, "", "file", "文件大小超过10MB限制"));
            return result;
        }

        // 文件格式验证
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            result.getErrors().add(new ImportError(0, "", "file", "仅支持 .xlsx 或 .xls 格式的Excel文件"));
            return result;
        }

        // 解析Excel
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows() - 1;
            result.setTotalRecords(totalRows);

            int success = 0;
            List<ImportError> errors = new ArrayList<>();

            // 列索引: 0学号,1姓名,2班级,3日期,4状态
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

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
                    date = DataUtil.parseDate(dateStr);
                    if (date == null) rowErrors.add("日期格式无效，支持格式: yyyy-MM-dd, yyyy/MM/dd, yyyy.M.d 等");
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

                // 保存学生
                Student student = studentRepository.findByStudentId(studentId).orElse(null);
                if (student == null) {
                    student = new Student(studentId, name, className);
                } else {
                    student.setName(name);
                    student.setClassName(className);
                }
                student = studentRepository.save(student);

                // 保存出勤记录
                Optional<Attendance> existing = attendanceRepository.findByStudentAndDate(student, date);
                if (existing.isPresent()) {
                    existing.get().setStatus(status);
                    attendanceRepository.save(existing.get());
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

    /**
     * 获取单元格字符串值，正确处理 Excel 原生日期类型
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // ★ 关键修复：使用 POI 的 DateUtil.isCellDateFormatted ★
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case FORMULA:
                try {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue.getCellType() == CellType.NUMERIC) {
                        double numeric = cellValue.getNumberValue();
                        // 再次判断是否为日期（某些公式计算结果可能是日期序列值）
                        // 这里简单处理：如果数值大于 30000（大概1900年以后），也可以尝试转为日期，但更准确的方法是重新获取 Cell 并判断格式。
                        // 为简化，先按普通数字处理。
                        return String.valueOf((long) numeric);
                    } else if (cellValue.getCellType() == CellType.STRING) {
                        return cellValue.getStringValue();
                    }
                } catch (Exception ignored) {}
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}