package com.example.attendance.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DataUtil {

    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    );

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = dateStr.trim();

        // 去掉时间部分
        if (trimmed.contains(" ")) {
            trimmed = trimmed.split(" ")[0];
        }

        // 处理 2026.5.27 -> 2026-05-27
        if (trimmed.matches("\\d{4}\\.\\d{1,2}\\.\\d{1,2}")) {
            String[] parts = trimmed.split("\\.");
            String year = parts[0];
            String month = String.format("%02d", Integer.parseInt(parts[1]));
            String day = String.format("%02d", Integer.parseInt(parts[2]));
            trimmed = year + "-" + month + "-" + day;
            try {
                return LocalDate.parse(trimmed);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
}