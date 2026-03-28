package com.example.attendance.controller.kaoqing;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/kaoqing")
public class AttendanceController {
    private static final List<Student> studentList = new ArrayList<>();
    private static final List<AttendanceRecord> attendanceList = new ArrayList<>();

    static {

        studentList.add(new Student("2024001", "张三", "计算机一班", 20));
        studentList.add(new Student("2024002", "李四", "计算机一班", 21));
        studentList.add(new Student("2024003", "王五", "计算机二班", 19));


        attendanceList.add(new AttendanceRecord("2024001", "2026-03-18", "正常"));
        attendanceList.add(new AttendanceRecord("2024002", "2026-03-18", "迟到"));
    }


    @GetMapping("/student/{studentId}")
    public Result<Student> getStudentById(@PathVariable String studentId) {
        for (Student student : studentList) {
            if (student.getStudentId().equals(studentId)) {
                return Result.success(student);
            }
        }
        return Result.error("学生ID：" + studentId + " 不存在");
    }


    @GetMapping("/students")
    public Result<List<Student>> getStudentList(
            @RequestParam String className,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int pageSize) {

        List<Student> classStudents = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getClassName().equals(className)) {
                classStudents.add(student);
            }
        }

        int total = classStudents.size();
        int start = (page - 1) * pageSize;
        if (start >= total) {
            return Result.success("暂无更多学生", new ArrayList<>());
        }
        int end = Math.min(start + pageSize, total);

        List<Student> pageStudents = new ArrayList<>(classStudents.subList(start, end));

        return Result.success("查询成功（共" + total + "条）", pageStudents);
    }


    @PostMapping("/attendance/update")
    public Result<String> updateAttendance(@RequestBody AttendanceRecord record) {
        // 第一步：校验学生是否存在
        boolean studentExist = false;
        for (Student student : studentList) {
            if (student.getStudentId().equals(record.getStudentId())) {
                studentExist = true;
                break;
            }
        }
        if (!studentExist) {
            return Result.error("学生ID不存在，无法更新考勤");
        }


        List<String> validStatus = new ArrayList<>();
        validStatus.add("正常");
        validStatus.add("迟到");
        validStatus.add("缺勤");
        validStatus.add("请假");
        if (record.getStatus() == null || !validStatus.contains(record.getStatus())) {
            return Result.error("考勤状态只能是：正常/迟到/缺勤/请假");
        }


        if (record.getAttendanceDate() == null || record.getAttendanceDate().trim().isEmpty()) {
            return Result.error("考勤日期不能为空");
        }


        boolean updated = false;
        for (AttendanceRecord ar : attendanceList) {
            if (ar.getStudentId().equals(record.getStudentId())
                    && ar.getAttendanceDate().equals(record.getAttendanceDate())) {
                ar.setStatus(record.getStatus());
                updated = true;
                break;
            }
        }
        if (!updated) {
            record.setRecordId(UUID.randomUUID().toString());
            attendanceList.add(record);
        }

        return Result.success(updated ? "考勤更新成功" : "考勤新增成功");
    }
}