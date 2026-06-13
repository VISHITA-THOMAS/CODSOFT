package com.currencyconverter.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public static boolean isValidAmount(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try { double v = Double.parseDouble(s.trim()); return v > 0 && Double.isFinite(v); }
        catch (NumberFormatException e) { return false; }
    }

    public static double parseAmount(String s) { return Double.parseDouble(s.trim()); }

    public static String nowTimestamp() { return LocalDateTime.now().format(FMT); }
}
