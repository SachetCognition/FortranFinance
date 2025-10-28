package com.mrffl.config;

/**
 * Configuration module for MRFFL (MR Fortran Finance Library).
 * 
 * This module defines type aliases and constants used throughout the library
 * to ensure consistent precision and formatting across all financial calculations.
 * 
 * Corresponds to MRFFL/src/mrffl_config.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class MrfflConfig {
    
    /**
     * Zero epsilon - tolerance for comparing floating-point values to zero.
     * Used throughout the library for numerical comparisons.
     * 
     * Fortran equivalent: zero_epsilon = 1.0d-8
     */
    public static final double ZERO_EPSILON = 1.0e-8;
    
    /**
     * Cash number format string for displaying monetary values.
     * Default format shows 15 characters wide with 4 decimal places.
     * 
     * Fortran equivalent: mrfflcnfmt = "f15.4"
     * 
     * Usage example:
     * <pre>
     * String formatted = String.format("%15.4f", cashValue);
     * </pre>
     */
    public static final String CASH_NUMBER_FORMAT = "%15.4f";
    
    /**
     * Cash title format string for displaying headers/titles.
     * Default format shows 15 characters wide, left-aligned.
     * 
     * Fortran equivalent: mrfflctfmt = "a15"
     * 
     * Usage example:
     * <pre>
     * String formatted = String.format("%-15s", titleText);
     * </pre>
     */
    public static final String CASH_TITLE_FORMAT = "%-15s";
    
    /**
     * Format a cash value using the standard cash number format.
     * 
     * @param value the monetary value to format
     * @return formatted string representation of the value
     */
    public static String formatCashValue(double value) {
        return String.format(CASH_NUMBER_FORMAT, value);
    }
    
    /**
     * Format a title using the standard cash title format.
     * 
     * @param title the title text to format
     * @return formatted string representation of the title
     */
    public static String formatCashTitle(String title) {
        return String.format(CASH_TITLE_FORMAT, title);
    }
    
    /**
     * Compare a double value to zero within tolerance.
     * 
     * @param value the value to compare
     * @return true if the absolute value is less than ZERO_EPSILON
     */
    public static boolean isZero(double value) {
        return Math.abs(value) < ZERO_EPSILON;
    }
    
    /**
     * Compare two double values for equality within tolerance.
     * 
     * @param a first value
     * @param b second value
     * @return true if the absolute difference is less than ZERO_EPSILON
     */
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < ZERO_EPSILON;
    }
    
    private MrfflConfig() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
