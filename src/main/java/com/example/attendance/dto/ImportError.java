package com.example.attendance.dto;

public class ImportError {
    private int row;
    private String studentId;
    private String field;
    private String message;

    public ImportError(int row, String studentId, String field, String message) {
        this.row = row;
        this.studentId = studentId;
        this.field = field;
        this.message = message;
    }

    // Getters
    public int getRow() { return row; }
    public String getStudentId() { return studentId; }
    public String getField() { return field; }
    public String getMessage() { return message; }
}