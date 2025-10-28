package com.mrffl.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Percentages utility class.
 * Based on MRFFL/unit_tests/utest_percentages.f90
 */
class PercentagesTest {
    
    private static final double EPSILON = 1.0e-10;
    
    @Test
    void testPercentageToFraction() {
        assertEquals(0.05, Percentages.percentageToFraction(5.0), EPSILON);
        assertEquals(0.10, Percentages.percentageToFraction(10.0), EPSILON);
        assertEquals(1.00, Percentages.percentageToFraction(100.0), EPSILON);
        assertEquals(0.00, Percentages.percentageToFraction(0.0), EPSILON);
    }
    
    @Test
    void testFractionToPercentage() {
        assertEquals(5.0, Percentages.fractionToPercentage(0.05), EPSILON);
        assertEquals(10.0, Percentages.fractionToPercentage(0.10), EPSILON);
        assertEquals(100.0, Percentages.fractionToPercentage(1.00), EPSILON);
        assertEquals(0.0, Percentages.fractionToPercentage(0.00), EPSILON);
    }
    
    @Test
    void testPercentageOf() {
        assertEquals(5.0, Percentages.percentageOf(100.0, 5.0), EPSILON);
        assertEquals(10.0, Percentages.percentageOf(100.0, 10.0), EPSILON);
        assertEquals(50.0, Percentages.percentageOf(100.0, 50.0), EPSILON);
        assertEquals(0.0, Percentages.percentageOf(100.0, 0.0), EPSILON);
    }
    
    @Test
    void testAddPercentage() {
        assertEquals(105.0, Percentages.addPercentage(100.0, 5.0), EPSILON);
        assertEquals(110.0, Percentages.addPercentage(100.0, 10.0), EPSILON);
        assertEquals(200.0, Percentages.addPercentage(100.0, 100.0), EPSILON);
        assertEquals(95.0, Percentages.addPercentage(100.0, -5.0), EPSILON);
    }
    
    @Test
    void testPercentageChange() {
        assertEquals(5.0, Percentages.percentageChange(100.0, 105.0), EPSILON);
        assertEquals(10.0, Percentages.percentageChange(100.0, 110.0), EPSILON);
        assertEquals(-5.0, Percentages.percentageChange(100.0, 95.0), EPSILON);
        assertEquals(100.0, Percentages.percentageChange(100.0, 200.0), EPSILON);
    }
    
    @Test
    void testPercentageOfTotal() {
        assertEquals(25.0, Percentages.percentageOfTotal(200.0, 50.0), EPSILON);
        assertEquals(50.0, Percentages.percentageOfTotal(200.0, 100.0), EPSILON);
        assertEquals(10.0, Percentages.percentageOfTotal(1000.0, 100.0), EPSILON);
    }
    
    @Test
    void testPercentageOfTotalZeroTotal() {
        assertThrows(ArithmeticException.class, () -> {
            Percentages.percentageOfTotal(0.0, 50.0);
        });
    }
    
    @Test
    void testPercentageToFractionArray() {
        double[] input = {5.0, 10.0, 15.0};
        double[] expected = {0.05, 0.10, 0.15};
        double[] result = Percentages.percentageToFraction(input);
        
        assertArrayEquals(expected, result, EPSILON);
    }
    
    @Test
    void testFractionToPercentageArray() {
        double[] input = {0.05, 0.10, 0.15};
        double[] expected = {5.0, 10.0, 15.0};
        double[] result = Percentages.fractionToPercentage(input);
        
        assertArrayEquals(expected, result, EPSILON);
    }
}
