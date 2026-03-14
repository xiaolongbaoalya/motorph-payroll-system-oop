package com.compprog1282025.service;

public final class MonetaryInputParser {
    private MonetaryInputParser() {
    }

    public static double parseAmount(String raw, String fieldName) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }

        String normalized = raw.trim().replace(",", "");
        if (!normalized.matches("-?\\d+(?:\\.\\d+)?")) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }

        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }
}