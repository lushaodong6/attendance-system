package com.example.attendance.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    // 任务一：学生信息接口（GET）
    @GetMapping("/info")
    public String getStudentInfo() {
        return "姓名：张三，学号：2023001，班级：计算机2班";
    }

    // 任务二：考勤打卡接口（POST）
    @PostMapping("/attendance")
    public String checkAttendance(@RequestBody Map<String,String>params) {
        String studentId = params.get("studentId");
        return "学号为 " + studentId + " 的学生打卡成功！";
    }

    // 任务三：课程列表接口（GET）
    @GetMapping("/courses")
    public List<String> getCourseList() {
        return Arrays.asList(
                "Java程序设计",
                "Python开发实践",
                "计算机网络",
                "数据结构与算法"
        );
    }
}
