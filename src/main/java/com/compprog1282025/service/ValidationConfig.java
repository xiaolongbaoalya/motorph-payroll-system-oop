package com.compprog1282025.service;

public final class ValidationConfig {
    private static final int DEFAULT_MIN_EMPLOYMENT_AGE = 16;
    private static final String MIN_EMPLOYMENT_AGE_PROPERTY = "motorph.minEmploymentAge";

    private ValidationConfig() {
    }

    public static int minEmploymentAge() {
        String configured = System.getProperty(MIN_EMPLOYMENT_AGE_PROPERTY);
        if (configured == null || configured.trim().isEmpty()) {
            return DEFAULT_MIN_EMPLOYMENT_AGE;
        }

        try {
            int parsed = Integer.parseInt(configured.trim());
            if (parsed < 0 || parsed > 100) {
                return DEFAULT_MIN_EMPLOYMENT_AGE;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return DEFAULT_MIN_EMPLOYMENT_AGE;
        }
    }
}