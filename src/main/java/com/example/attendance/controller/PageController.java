package com.example.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 只映射统计页面，不要写 @GetMapping("/")
    @GetMapping("/statistics")
    public String statisticsPage() {
        return "statistics";
    }
}