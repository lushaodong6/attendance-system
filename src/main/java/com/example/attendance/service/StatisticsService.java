package com.example.attendance.service;

import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * 按周统计出勤率
     */
    public StatisticsDTO getWeeklyStatistics(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return calculateStatistics(startOfWeek, endOfWeek, "WEEK", startOfWeek.toString() + " 至 " + endOfWeek.toString());
    }

    /**
     * 按月统计出勤率
     */
    public StatisticsDTO getMonthlyStatistics(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        return calculateStatistics(startOfMonth, endOfMonth, "MONTH", year + "-" + String.format("%02d", month));
    }

    /**
     * 班级整体出勤率统计
     */
    public StatisticsDTO getClassStatistics(String className, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByClassNameAndDateBetween(className, startDate, endDate);
        Map<String, Long> statusCounts = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));

        long total = attendances.size();
        long present = statusCounts.getOrDefault("出勤", 0L);
        BigDecimal rate = total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(present).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        StatisticsDTO dto = new StatisticsDTO();
        dto.setPeriod("CLASS");
        dto.setPeriodValue(className);
        dto.setOverallRate(rate);
        dto.setStatusCounts(statusCounts);
        return dto;
    }

    /**
     * 所有班级整体出勤率统计 (按班级分组)
     */
    public Map<String, BigDecimal> getAllClassesRates(LocalDate startDate, LocalDate endDate) {
        List<Student> allStudents = studentRepository.findAll();
        Map<String, List<Attendance>> classAttendanceMap = new HashMap<>();

        for (Student student : allStudents) {
            List<Attendance> attendances = attendanceRepository.findByStudent(student)
                    .stream()
                    .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
            classAttendanceMap.computeIfAbsent(student.getClassName(), k -> new ArrayList<>()).addAll(attendances);
        }

        Map<String, BigDecimal> result = new HashMap<>();
        for (Map.Entry<String, List<Attendance>> entry : classAttendanceMap.entrySet()) {
            String className = entry.getKey();
            List<Attendance> list = entry.getValue();
            long total = list.size();
            long present = list.stream().filter(a -> "出勤".equals(a.getStatus())).count();
            BigDecimal rate = total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(present).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            result.put(className, rate);
        }
        return result;
    }

    private StatisticsDTO calculateStatistics(LocalDate startDate, LocalDate endDate, String periodType, String periodValue) {
        List<Attendance> attendances = attendanceRepository.findByDateBetween(startDate, endDate);
        // 按班级分组计算
        Map<String, List<Attendance>> classMap = new HashMap<>();
        for (Attendance a : attendances) {
            String className = a.getStudent().getClassName();
            classMap.computeIfAbsent(className, k -> new ArrayList<>()).add(a);
        }

        Map<String, BigDecimal> classRates = new HashMap<>();
        long totalAll = 0;
        long presentAll = 0;
        for (Map.Entry<String, List<Attendance>> entry : classMap.entrySet()) {
            List<Attendance> list = entry.getValue();
            long total = list.size();
            long present = list.stream().filter(a -> "出勤".equals(a.getStatus())).count();
            totalAll += total;
            presentAll += present;
            BigDecimal rate = total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(present).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            classRates.put(entry.getKey(), rate);
        }

        BigDecimal overallRate = totalAll == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(presentAll).divide(BigDecimal.valueOf(totalAll), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        // 各状态统计
        Map<String, Long> statusCounts = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));

        StatisticsDTO dto = new StatisticsDTO();
        dto.setPeriod(periodType);
        dto.setPeriodValue(periodValue);
        dto.setOverallRate(overallRate);
        dto.setClassRates(classRates);
        dto.setStatusCounts(statusCounts);
        return dto;
    }
}