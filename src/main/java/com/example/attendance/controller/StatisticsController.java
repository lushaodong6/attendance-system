package com.example.attendance.controller;

import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/api/statistics/week")
    public StatisticsDTO weeklyStats(@RequestParam(value = "date", required = false) String date) {
        LocalDate localDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
        return statisticsService.getWeeklyStatistics(localDate);
    }

    @GetMapping("/api/statistics/month")
    public StatisticsDTO monthlyStats(@RequestParam int year, @RequestParam int month) {
        return statisticsService.getMonthlyStatistics(year, month);
    }

    @GetMapping("/api/statistics/class")
    public StatisticsDTO classStats(@RequestParam String className,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getClassStatistics(className, start, end);
    }

    @GetMapping("/api/statistics/classes-rates")
    public Map<String, BigDecimal> allClassesRates(@RequestParam String startDate,
                                                   @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getAllClassesRates(start, end);
    }
}