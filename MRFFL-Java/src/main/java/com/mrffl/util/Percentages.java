package com.mrffl.util;

import com.mrffl.config.MrfflConfig;

/**
 * Utility functions for working with percentages in financial calculations.
 * 
 * This module provides simple conversion and calculation functions for
 * percentages and fractions. All functions are pure (no side effects)
 * and can handle both scalar and array operations.
 * 
 * Corresponds to MRFFL/src/mrffl_percentages.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class Percentages {
    
    /**
     * Convert a percentage to a fraction.
     * 
     * @param percentage the percentage value (e.g., 5.0 for 5%)
     * @return the fractional equivalent (e.g., 0.05)
     * 
     * Example: percentageToFraction(5.0) returns 0.05
     */
    public static double percentageToFraction(double percentage) {
        return percentage / 100.0;
    }
    
    /**
     * Convert a percentage to a fraction for an array of values.
     * 
     * @param percentages array of percentage values
     * @return array of fractional equivalents
     */
    public static double[] percentageToFraction(double[] percentages) {
        double[] result = new double[percentages.length];
        for (int i = 0; i < percentages.length; i++) {
            result[i] = percentageToFraction(percentages[i]);
        }
        return result;
    }
    
    /**
     * Convert a fraction to a percentage.
     * 
     * @param fraction the fractional value (e.g., 0.05)
     * @return the percentage equivalent (e.g., 5.0)
     * 
     * Example: fractionToPercentage(0.05) returns 5.0
     */
    public static double fractionToPercentage(double fraction) {
        return fraction * 100.0;
    }
    
    /**
     * Convert a fraction to a percentage for an array of values.
     * 
     * @param fractions array of fractional values
     * @return array of percentage equivalents
     */
    public static double[] fractionToPercentage(double[] fractions) {
        double[] result = new double[fractions.length];
        for (int i = 0; i < fractions.length; i++) {
            result[i] = fractionToPercentage(fractions[i]);
        }
        return result;
    }
    
    /**
     * Compute a percentage of a value.
     * 
     * @param value the base value
     * @param percentage the percentage to compute (e.g., 5.0 for 5%)
     * @return the percentage of the value
     * 
     * Example: percentageOf(100.0, 5.0) returns 5.0
     */
    public static double percentageOf(double value, double percentage) {
        return value * percentage / 100.0;
    }
    
    /**
     * Compute a percentage of each value in an array.
     * 
     * @param values array of base values
     * @param percentage the percentage to compute
     * @return array of computed percentages
     */
    public static double[] percentageOf(double[] values, double percentage) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = percentageOf(values[i], percentage);
        }
        return result;
    }
    
    /**
     * Add a percentage to a value.
     * Equivalent to value * (1 + percentage/100).
     * 
     * @param value the base value
     * @param percentage the percentage to add (e.g., 5.0 for 5%)
     * @return the value increased by the percentage
     * 
     * Example: addPercentage(100.0, 5.0) returns 105.0
     */
    public static double addPercentage(double value, double percentage) {
        return value * (1.0 + percentage / 100.0);
    }
    
    /**
     * Add a percentage to each value in an array.
     * 
     * @param values array of base values
     * @param percentage the percentage to add
     * @return array of increased values
     */
    public static double[] addPercentage(double[] values, double percentage) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = addPercentage(values[i], percentage);
        }
        return result;
    }
    
    /**
     * Compute the percentage change between two values.
     * Returns 100 * (valueTo - valueFrom) / valueFrom
     * 
     * @param valueFrom the starting value
     * @param valueTo the ending value
     * @return the percentage change
     * 
     * Example: percentageChange(100.0, 105.0) returns 5.0
     */
    public static double percentageChange(double valueFrom, double valueTo) {
        return 100.0 * (valueTo - valueFrom) / valueFrom;
    }
    
    /**
     * Compute what percentage one value is of another (part as percentage of total).
     * Returns 100 * valuePart / valueTotal
     * 
     * @param valueTotal the total value
     * @param valuePart the part value
     * @return the percentage that valuePart represents of valueTotal
     * @throws ArithmeticException if valueTotal is too close to zero
     * 
     * Example: percentageOfTotal(200.0, 50.0) returns 25.0
     */
    public static double percentageOfTotal(double valueTotal, double valuePart) {
        if (MrfflConfig.isZero(valueTotal)) {
            throw new ArithmeticException("Cannot compute percentage of total when total is zero");
        }
        return 100.0 * valuePart / valueTotal;
    }
    
    private Percentages() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
