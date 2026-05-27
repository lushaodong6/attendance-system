package com.example.attendance.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentName;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private boolean late;        // 迟到
    private boolean earlyLeave;  // 早退

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // Constructors
    public Attendance() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public LocalDateTime getSignInTime() { return signInTime; }
    public void setSignInTime(LocalDateTime signInTime) { this.signInTime = signInTime; }
    public LocalDateTime getSignOutTime() { return signOutTime; }
    public void setSignOutTime(LocalDateTime signOutTime) { this.signOutTime = signOutTime; }
    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }
    public boolean isEarlyLeave() { return earlyLeave; }
    public void setEarlyLeave(boolean earlyLeave) { this.earlyLeave = earlyLeave; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}