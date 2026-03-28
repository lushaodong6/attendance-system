package com.example.attendance.controller.kaoqing;

public class AttendanceRecord {
        private String studentId;
        private String attendanceDate;
        private String status;

    public AttendanceRecord() {
    }

    public AttendanceRecord(  String studentId,String attendanceDate,String status) {

        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                " studentId='" + studentId + '\'' +
                ", attendanceDate='" + attendanceDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public void setRecordId(String string) {
    }
}
