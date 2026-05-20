package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 测试接口（必须放在最前面以确认可用）
    @GetMapping("/api/list")
    @ResponseBody
    public List<Student> apiList() {
        return studentService.getAllStudents();
    }

    @GetMapping("/api/edit/{id}")
    @ResponseBody
    public Student apiEdit(@PathVariable Integer id) {
        return studentService.getStudentById(id);
    }

    // 页面跳转
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "student-list";
    }

    @GetMapping("/add")
    public String add() {
        return "student-add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student-edit";
    }
}