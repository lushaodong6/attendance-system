package com.example.attendance.config;

import com.example.attendance.entity.Course;
import com.example.attendance.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            // 使用 LocalTime.of() 而不是 LocalDateTime
            Course c1 = new Course("Java编程实战", LocalTime.of(14, 0, 0), LocalTime.of(16, 0, 0));
            Course c2 = new Course("数据库设计", LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0));
            courseRepository.save(c1);
            courseRepository.save(c2);
            System.out.println("测试课程已初始化");
        }
    }
}