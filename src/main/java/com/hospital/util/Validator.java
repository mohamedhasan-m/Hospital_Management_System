package com.hospital.util;

/**
 * Simple input checks used by the service classes.
 * When something is wrong they throw IllegalArgumentException with a message
 * that is shown to the user (the servlet turns it into HTTP 400).
 */
public class Validator {

    private Validator() { }

    /** Text that must not be empty. */
    public static String required(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return limit(value.trim(), field, maxLength);
    }

    /** Text that may be empty. Returns "" when nothing was entered. */
    public static String optional(String value, String field, int maxLength) {
        if (value == null) {
            return "";
        }
        return limit(value.trim(), field, maxLength);
    }

    private static String limit(String value, String field, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters");
        }
        return value;
    }

    /** Whole number between min and max. */
    public static int integer(String value, String field, int min, int max) {
        String text = required(value, field, 20);
        try {
            int number = Integer.parseInt(text);
            if (number < min || number > max) {
                throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be a whole number");
        }
    }

    /** Money amount (0 or more). An empty value counts as 0. Rounded to 2 decimals. */
    public static double amount(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        double number;
        try {
            number = Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be a number");
        }
        if (Double.isNaN(number) || Double.isInfinite(number) || number < 0 || number > 10000000) {
            throw new IllegalArgumentException(field + " must be between 0 and 10000000");
        }
        return Math.round(number * 100) / 100.0;
    }

    /** Phone number: exactly 10 digits. */
    public static String phone(String value) {
        String phone = required(value, "Phone", 15);
        if (!phone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone must be exactly 10 digits");
        }
        return phone;
    }

    /** Optional email, but if given it must look like name@domain.com */
    public static String email(String value) {
        String email = optional(value, "Email", 100);
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email is not valid");
        }
        return email;
    }

    /** The value must be one of the allowed words (used for gender). */
    public static String oneOf(String value, String field, String... allowed) {
        String text = required(value, field, 20);
        for (String option : allowed) {
            if (option.equals(text)) {
                return text;
            }
        }
        throw new IllegalArgumentException(field + " is not valid");
    }
}
