package com.example.attendance.dto;

import java.util.ArrayList;
import java.util.List;

public class UploadResult {
    private int totalRecords;
    private int successCount;
    private int failCount;
    private List<ImportError> errors = new ArrayList<>();

    // Getters and Setters
    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
    public List<ImportError> getErrors() { return errors; }
    public void setErrors(List<ImportError> errors) { this.errors = errors; }
}