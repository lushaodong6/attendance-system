package com.example.attendance.controller;

import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public String statisticsPage() {
        return "statistics";
    }

    @GetMapping("/api/statistics/week")
    @ResponseBody
    public StatisticsDTO weeklyStats(@RequestParam(value = "date", required = false) String date) {
        LocalDate localDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
        return statisticsService.getWeeklyStatistics(localDate);
    }

    @GetMapping("/api/statistics/month")
    @ResponseBody
    public StatisticsDTO monthlyStats(@RequestParam int year, @RequestParam int month) {
        return statisticsService.getMonthlyStatistics(year, month);
    }

    @GetMapping("/api/statistics/class")
    @ResponseBody
    public StatisticsDTO classStats(@RequestParam String className,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getClassStatistics(className, start, end);
    }

    @GetMapping("/api/statistics/classes-rates")
    @ResponseBody
    public Map<String, BigDecimal> allClassesRates(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getAllClassesRates(start, end);
    }
}