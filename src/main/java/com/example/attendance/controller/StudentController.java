package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // ==================== 页面跳转 ====================

    /**
     * 学生列表页面（支持搜索和排序）
     */
    @GetMapping("/list")
    public String studentList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            Model model) {

        List<Student> students;

        // 如果有搜索关键词
        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentService.searchStudents(keyword.trim());
            model.addAttribute("keyword", keyword);
        } else {
            // 根据排序方式查询
            switch (sort) {
                case "studentNo":
                    students = studentService.getAllStudentsOrderByStudentNo();
                    break;
                case "name":
                    students = studentService.getAllStudentsOrderByName();
                    break;
                default:
                    students = studentService.getAllStudents();
                    break;
            }
        }

        model.addAttribute("students", students);
        model.addAttribute("sort", sort);
        return "student-list";
    }

    /**
     * 新增学生页面
     */
    @GetMapping("/add")
    public String addStudentPage(Model model) {
        model.addAttribute("student", new Student());
        return "student-add";
    }

    /**
     * 修改学生页面
     */
    @GetMapping("/edit/{id}")
    public String editStudentPage(@PathVariable Integer id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "student-edit";
    }

    // ==================== 表单提交处理 ====================

    /**
     * 保存新增学生（带验证）
     */
    @PostMapping("/save")
    public String saveStudent(
            @RequestParam String studentNo,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            RedirectAttributes redirectAttributes) {

        // ========== 表单验证 ==========
        StringBuilder errors = new StringBuilder();

        // 验证学号（必填）
        if (studentNo == null || studentNo.trim().isEmpty()) {
            errors.append("学号不能为空！\\n");
        } else if (!studentNo.matches("^\\d{6,10}$")) {
            errors.append("学号格式不正确（应为6-10位数字）！\\n");
        }

        // 验证姓名（必填）
        if (name == null || name.trim().isEmpty()) {
            errors.append("姓名不能为空！\\n");
        } else if (name.trim().length() < 2 || name.trim().length() > 20) {
            errors.append("姓名长度应为2-20个字符！\\n");
        }

        // 验证性别（必填）
        if (gender == null || gender.trim().isEmpty()) {
            errors.append("请选择性别！\\n");
        }

        // 验证出生日期（必填）
        if (birthDate == null) {
            errors.append("出生日期不能为空！\\n");
        } else if (birthDate.isAfter(LocalDate.now())) {
            errors.append("出生日期不能是未来日期！\\n");
        }

        // 验证手机号（格式检查）
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                errors.append("手机号格式不正确！\\n");
            }
        }

        // 验证邮箱（格式检查）
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[\\w-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                errors.append("邮箱格式不正确！\\n");
            }
        }

        // 如果有错误，返回错误信息
        if (errors.length() > 0) {
            redirectAttributes.addFlashAttribute("error", errors.toString());
            return "redirect:/student/add";
        }

        // 创建学生对象
        Student student = new Student();
        student.setStudentNo(studentNo.trim());
        student.setName(name.trim());
        student.setGender(gender);
        student.setBirthDate(birthDate);
        student.setPhone(phone != null ? phone.trim() : "");
        student.setEmail(email != null ? email.trim() : "");
        student.setAddress(address != null ? address.trim() : "");

        // 保存学生
        boolean success = studentService.addStudent(student);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "学生信息添加成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "添加失败，请重试！");
        }

        return "redirect:/student/list";
    }

    /**
     * 保存修改学生（带验证）
     */
    @PostMapping("/update")
    public String updateStudent(
            @RequestParam Integer id,
            @RequestParam String studentNo,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            RedirectAttributes redirectAttributes) {

        // ========== 表单验证 ==========
        StringBuilder errors = new StringBuilder();

        if (studentNo == null || studentNo.trim().isEmpty()) {
            errors.append("学号不能为空！\\n");
        } else if (!studentNo.matches("^\\d{6,10}$")) {
            errors.append("学号格式不正确！\\n");
        }

        if (name == null || name.trim().isEmpty()) {
            errors.append("姓名不能为空！\\n");
        }

        if (gender == null || gender.trim().isEmpty()) {
            errors.append("请选择性别！\\n");
        }

        if (birthDate == null) {
            errors.append("出生日期不能为空！\\n");
        } else if (birthDate.isAfter(LocalDate.now())) {
            errors.append("出生日期不能是未来日期！\\n");
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                errors.append("手机号格式不正确！\\n");
            }
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[\\w-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                errors.append("邮箱格式不正确！\\n");
            }
        }

        if (errors.length() > 0) {
            redirectAttributes.addFlashAttribute("error", errors.toString());
            return "redirect:/student/edit/" + id;
        }

        Student student = new Student();
        student.setId(id);
        student.setStudentNo(studentNo.trim());
        student.setName(name.trim());
        student.setGender(gender);
        student.setBirthDate(birthDate);
        student.setPhone(phone != null ? phone.trim() : "");
        student.setEmail(email != null ? email.trim() : "");
        student.setAddress(address != null ? address.trim() : "");

        boolean success = studentService.updateStudent(student);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "学生信息修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "修改失败，请重试！");
        }

        return "redirect:/student/list";
    }

    /**
     * 删除学生
     */
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean success = studentService.deleteStudent(id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "删除成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "删除失败！");
        }
        return "redirect:/student/list";
    }

    /**
     * 批量删除学生
     */
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestParam("ids") String ids, RedirectAttributes redirectAttributes) {
        if (ids == null || ids.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择要删除的学生！");
            return "redirect:/student/list";
        }

        String[] idArray = ids.split(",");
        List<Integer> idList = Arrays.stream(idArray)
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toList());

        boolean success = studentService.batchDeleteStudents(idList);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "批量删除成功！共删除 " + idList.size() + " 条记录");
        } else {
            redirectAttributes.addFlashAttribute("error", "批量删除失败！");
        }

        return "redirect:/student/list";
    }
}
