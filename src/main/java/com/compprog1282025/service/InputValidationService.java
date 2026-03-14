package com.compprog1282025.service;

import java.util.Locale;
import java.util.regex.Pattern;

final class InputValidationService {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+(?: [A-Za-z]+)*$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9-]{1,15}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9\\s.,#\\-/]+$");
    private static final Pattern LABEL_PATTERN = Pattern.compile("^[A-Za-z0-9\\s&.,():\\-/]+$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern SSS_PATTERN = Pattern.compile("^\\d{2}-\\d{7}-\\d$");
    private static final Pattern PHILHEALTH_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern TIN_PATTERN = Pattern.compile("^\\d{3}-\\d{3}-\\d{3}-\\d{3}$");
    private static final Pattern PAG_IBIG_PATTERN = Pattern.compile("^\\d{12}$");

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_ADDRESS_LENGTH = 255;
    private static final int MAX_LABEL_LENGTH = 80;
    private static final int MAX_REASON_LENGTH = 500;
    private static final int MAX_GOV_ID_LENGTH = 32;

    private InputValidationService() {
    }

    static String requirePersonName(String fieldName, String value) {
        String sanitized = requireNonBlank(fieldName, value);
        rejectInjection(fieldName, sanitized);
        requireMaxLength(fieldName, sanitized, MAX_NAME_LENGTH);
        if (!NAME_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " must contain letters only.");
        }
        return sanitized;
    }

    static String requirePhoneNumber(String value) {
        String sanitized = requireNonBlank("Phone Number", value);
        rejectInjection("Phone Number", sanitized);
        requireMaxLength("Phone Number", sanitized, 15);
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid Phone Number. Only numbers and dashes are allowed (max 15 characters).");
        }
        return sanitized;
    }

    static String requireAddress(String value) {
        String sanitized = requireNonBlank("Address", value);
        rejectInjection("Address", sanitized);
        requireMaxLength("Address", sanitized, MAX_ADDRESS_LENGTH);
        if (!ADDRESS_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid input: Address contains unsupported characters.");
        }
        return sanitized;
    }

    static String requireLabel(String fieldName, String value) {
        String sanitized = requireNonBlank(fieldName, value);
        rejectInjection(fieldName, sanitized);
        requireMaxLength(fieldName, sanitized, MAX_LABEL_LENGTH);
        if (!LABEL_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " has invalid characters.");
        }
        return sanitized;
    }


    static String requirePositionTitle(String value) {
        String sanitized = requireLabel("Position", value);
        if (!sanitized.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Invalid input: Position must contain at least one letter.");
        }
        return sanitized;
    }
    static String requireReason(String fieldName, String value) {
        String sanitized = requireNonBlank(fieldName, value);
        rejectInjection(fieldName, sanitized);
        requireMaxLength(fieldName, sanitized, MAX_REASON_LENGTH);
        return sanitized;
    }

    static String requireTime(String fieldName, String value) {
        String sanitized = requireNonBlank(fieldName, value);
        rejectInjection(fieldName, sanitized);
        if (!TIME_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " must use HH:mm format.");
        }
        return sanitized;
    }

    static String requireSssNumber(String value) {
        String sanitized = requireNonBlank("SSS Number", value);
        rejectInjection("SSS Number", sanitized);
        requireMaxLength("SSS Number", sanitized, MAX_GOV_ID_LENGTH);
        if (!SSS_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid SSS Number. Format must be 00-0000000-0.");
        }
        return sanitized;
    }

    static String requirePhilHealthNumber(String value) {
        String sanitized = requireNonBlank("PhilHealth Number", value);
        rejectInjection("PhilHealth Number", sanitized);
        requireMaxLength("PhilHealth Number", sanitized, MAX_GOV_ID_LENGTH);
        if (!PHILHEALTH_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid PhilHealth Number. Must contain exactly 12 digits.");
        }
        return sanitized;
    }

    static String requireTinNumber(String value) {
        String sanitized = requireNonBlank("TIN Number", value);
        rejectInjection("TIN Number", sanitized);
        requireMaxLength("TIN Number", sanitized, MAX_GOV_ID_LENGTH);
        if (!TIN_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid TIN Number. Format must be 000-000-000-000.");
        }
        return sanitized;
    }

    static String requirePagIbigNumber(String value) {
        String sanitized = requireNonBlank("Pag-IBIG Number", value);
        rejectInjection("Pag-IBIG Number", sanitized);
        requireMaxLength("Pag-IBIG Number", sanitized, MAX_GOV_ID_LENGTH);
        if (!PAG_IBIG_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid Pag-IBIG Number. Must contain exactly 12 digits.");
        }
        return sanitized;
    }

    static void requireNonNegative(String fieldName, double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " must not be negative.");
        }
    }

    private static void requireMaxLength(String fieldName, String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " must not exceed " + maxLength + " characters.");
        }
    }

    private static String requireNonBlank(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " must not be empty.");
        }
        return value.trim();
    }

    static void rejectInjection(String fieldName, String value) {
        String upper = value.toUpperCase(Locale.ROOT);
        if (upper.contains("' OR 1=1")
                || upper.contains(" OR 1=1")
                || upper.contains("DROP TABLE")
                || upper.contains("SELECT *")
                || upper.contains("UNION SELECT")
                || upper.contains("INSERT INTO")
                || upper.contains("DELETE FROM")
                || upper.contains("ALTER TABLE")
                || upper.contains("--")
                || upper.contains("/*")
                || upper.contains("*/")) {
            throw new IllegalArgumentException("Invalid input: " + fieldName + " contains disallowed or unsafe content.");
        }
    }
}
