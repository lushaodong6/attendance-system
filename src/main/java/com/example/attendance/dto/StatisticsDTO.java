package com.example.attendance.dto;

import java.math.BigDecimal;
import java.util.Map;

public class StatisticsDTO {
    private String period; // WEEK, MONTH, CLASS
    private String periodValue;
    private BigDecimal overallRate;
    private Map<String, BigDecimal> classRates;
    private Map<String, Long> statusCounts;

    // Getters and Setters
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getPeriodValue() { return periodValue; }
    public void setPeriodValue(String periodValue) { this.periodValue = periodValue; }
    public BigDecimal getOverallRate() { return overallRate; }
    public void setOverallRate(BigDecimal overallRate) { this.overallRate = overallRate; }
    public Map<String, BigDecimal> getClassRates() { return classRates; }
    public void setClassRates(Map<String, BigDecimal> classRates) { this.classRates = classRates; }
    public Map<String, Long> getStatusCounts() { return statusCounts; }
    public void setStatusCounts(Map<String, Long> statusCounts) { this.statusCounts = statusCounts; }
}