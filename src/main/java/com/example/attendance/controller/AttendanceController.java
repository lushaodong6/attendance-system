package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            Model model) {

        List<Attendance> attendances = attendanceService.filterAttendances(courseId, period, startDate, endDate, status);
        List<Course> courses = attendanceService.getAllCourses();

        model.addAttribute("attendances", attendances);
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedPeriod", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", status);
        return "index";
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam Long courseId,
                         @RequestParam String studentName,
                         @RequestParam(required = false) String autoSignTime,
                         RedirectAttributes redirectAttributes) {  // 关键修改：使用 RedirectAttributes
        LocalDateTime signTime;
        if (autoSignTime != null && !autoSignTime.isEmpty()) {
            signTime = LocalDateTime.parse(autoSignTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            signTime = LocalDateTime.now();
        }
        String result = attendanceService.signIn(courseId, studentName, signTime);
        redirectAttributes.addFlashAttribute("message", result);  // 关键修改：添加消息
        return "redirect:/";
    }

    @PostMapping("/signout/{id}")
    public String signOut(@PathVariable Long id, RedirectAttributes redirectAttributes) {  // 关键修改
        String result = attendanceService.signOut(id, LocalDateTime.now());
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/";
    }

    @GetMapping("/export")
    public void exportCsv(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {

        List<Attendance> list = attendanceService.filterAttendances(courseId, period, startDate, endDate, status);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"attendance_export.csv\"");
        PrintWriter writer = response.getWriter();

        writer.println("学生姓名,课程名称,签到时间,签退时间,迟到,早退");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Attendance att : list) {
            String signIn = att.getSignInTime() != null ? att.getSignInTime().format(fmt) : "";
            String signOut = att.getSignOutTime() != null ? att.getSignOutTime().format(fmt) : "";
            String late = att.isLate() ? "是" : "否";
            String early = att.isEarlyLeave() ? "是" : "否";
            writer.printf("%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(att.getStudentName()),
                    escapeCsv(att.getCourse().getName()),
                    signIn, signOut, late, early);
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}