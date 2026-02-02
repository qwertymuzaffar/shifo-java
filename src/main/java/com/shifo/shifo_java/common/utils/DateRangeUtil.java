package com.shifo.shifo_java.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateRangeUtil {

    public static LocalDate startOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    public static LocalDate endOfWeek(LocalDate date) {
        return date.with(DayOfWeek.SUNDAY);
    }

    public static LocalDate startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public static LocalDate endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    public static LocalDate startOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    public static LocalDate endOfYear(LocalDate date) {
        return date.withMonth(12).withDayOfMonth(31);
    }
}
