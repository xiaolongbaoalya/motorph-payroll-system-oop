package com.compprog1282025.service;

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter stringFormatter = DateTimeFormatter.ofPattern("H:mm");
	
	
	public static LocalDate convertStringToDate(String date) {
		LocalDate localDate = LocalDate.parse(date, dateFormatter);
		return localDate;
	}
	
	public static String convertDateToString(LocalDate date) {
		String dateString = date.format(dateFormatter);
		return dateString;
	}

    public static LocalTime convertStringToTime(String time) {
        // Safety Check: If the string is empty or null, don't try to parse it
        if (time == null || time.trim().isEmpty() || time.equalsIgnoreCase("N/A")) {
            return null;
        }

        try {
            return LocalTime.parse(time.trim(), stringFormatter);
        } catch (Exception e) {
            System.err.println("Warning: Could not parse time '" + time + "'. Returning null.");
            return null;
        }
    }
	
	public static String convertTimeToString(LocalTime time) {
		String timeString = time.format(stringFormatter);
		return timeString;
	}
	
	public static boolean isValidMonth(int month) {
		if (month >= 1 && month <= 12) {
			return true;
		}
		return false;
	}
	
	public static boolean isValidYear(int year) {
		if (year >= 1 && year <= 99999) {
			return true;
		}
		return false;
	}
	
	public static LocalDate getInvalidDate() {
		return LocalDate.of(-9999, 1, 1);
	}
}
