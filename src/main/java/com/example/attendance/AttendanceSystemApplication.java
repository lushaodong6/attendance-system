package com.example.attendance;

import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceSystemApplication implements CommandLineRunner {

    @Autowired
    private AttendanceService attendanceService;

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        attendanceService.initTestData();  // 初始化课程数据
    }
}