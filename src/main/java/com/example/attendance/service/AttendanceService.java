package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private CourseRepository courseRepository;

    // 签到逻辑：校验时间窗口，判断迟到
    @Transactional
    public String signIn(Long courseId, String studentName, LocalDateTime signTime) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 计算可打卡时间段：课程开始前15分钟 ～ 课程开始后30分钟
        LocalDateTime startLimit = LocalDateTime.of(signTime.toLocalDate(), course.getStartTime())
                .minusMinutes(15);
        LocalDateTime endLimit = LocalDateTime.of(signTime.toLocalDate(), course.getStartTime())
                .plusMinutes(30);

        if (signTime.isBefore(startLimit)) {
            return "打卡失败：未到打卡时间（课程开始前15分钟才可打卡）";
        }
        if (signTime.isAfter(endLimit)) {
            return "打卡失败：已超过打卡时段（课程开始后30分钟无法打卡）";
        }

        // 判断迟到：签到时间 > 课程开始时间
        LocalDateTime courseStartDateTime = LocalDateTime.of(signTime.toLocalDate(), course.getStartTime());
        boolean late = signTime.isAfter(courseStartDateTime);

        Attendance attendance = new Attendance();
        attendance.setCourse(course);
        attendance.setStudentName(studentName);
        attendance.setSignInTime(signTime);
        attendance.setLate(late);
        attendance.setEarlyLeave(false);  // 初始未早退
        attendanceRepository.save(attendance);

        return late ? "签到成功（迟到）" : "签到成功（正常）";
    }

    // 签退逻辑：判断早退
    @Transactional
    public String signOut(Long attendanceId, LocalDateTime signOutTime) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));
        if (attendance.getSignOutTime() != null) {
            return "已签退过，请勿重复操作";
        }
        attendance.setSignOutTime(signOutTime);

        // 早退判断：签退时间 < 课程结束时间
        Course course = attendance.getCourse();
        LocalDateTime courseEndDateTime = LocalDateTime.of(signOutTime.toLocalDate(), course.getEndTime());
        boolean earlyLeave = signOutTime.isBefore(courseEndDateTime);
        attendance.setEarlyLeave(earlyLeave);
        attendanceRepository.save(attendance);

        return earlyLeave ? "签退成功（早退）" : "签退成功（正常）";
    }

    // 多条件筛选考勤记录（课程、快速时间段、日期范围、状态）
    public List<Attendance> filterAttendances(Long courseId, String period,
                                              String startDateStr, String endDateStr,
                                              String status) {
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 课程筛选
            if (courseId != null && courseId > 0) {
                predicates.add(cb.equal(root.get("course").get("id"), courseId));
            }

            // 2. 时间范围：优先使用 startDate/endDate，否则根据 period 计算
            LocalDate startDate = null;
            LocalDate endDate = null;
            if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } else if (period != null && !period.isEmpty()) {
                LocalDate today = LocalDate.now();
                switch (period) {
                    case "today":
                        startDate = today;
                        endDate = today;
                        break;
                    case "week":
                        startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                        endDate = startDate.plusDays(6);
                        break;
                    case "month":
                        startDate = today.withDayOfMonth(1);
                        endDate = today.withDayOfMonth(today.lengthOfMonth());
                        break;
                }
            }
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("signInTime"), startDateTime, endDateTime));
            }

            // 3. 状态筛选
            if (status != null && !status.isEmpty()) {
                switch (status) {
                    case "normal":
                        predicates.add(cb.equal(root.get("late"), false));
                        predicates.add(cb.equal(root.get("earlyLeave"), false));
                        break;
                    case "late":
                        predicates.add(cb.equal(root.get("late"), true));
                        break;
                    case "early":
                        predicates.add(cb.equal(root.get("earlyLeave"), true));
                        break;
                    case "late_early":
                        predicates.add(cb.equal(root.get("late"), true));
                        predicates.add(cb.equal(root.get("earlyLeave"), true));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return attendanceRepository.findAll(spec);
    }

    // 获取所有课程
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 初始化测试数据（可选）
    @Transactional
    public void initTestData() {
        if (courseRepository.count() == 0) {
            courseRepository.save(new Course("Java编程实战", LocalTime.of(9, 0, 0), LocalTime.of(10, 30, 0)));
            courseRepository.save(new Course("Python数据分析", LocalTime.of(14, 0, 0), LocalTime.of(15, 30, 0)));
        }
        // 其他测试数据可自行添加
    }
}