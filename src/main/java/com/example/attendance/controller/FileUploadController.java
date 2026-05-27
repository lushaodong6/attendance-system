package com.example.attendance.controller;

import com.example.attendance.dto.UploadResult;
import com.example.attendance.service.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    @Autowired
    private ExcelUploadService excelUploadService;

    @GetMapping("/")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        UploadResult result = excelUploadService.processExcel(file);
        model.addAttribute("result", result);
        return "upload";
    }
}