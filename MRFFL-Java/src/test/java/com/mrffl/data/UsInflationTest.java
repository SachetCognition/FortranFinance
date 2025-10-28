package com.mrffl.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsInflationTest {
    
    private static final double TOLERANCE = 1e-6;
    
    @Test
    public void testInfDatArraySize() {
        int expectedSize = UsInflation.LAST_YEAR - UsInflation.FIRST_YEAR + 1;
        assertEquals(110, expectedSize);
        assertEquals(110, UsInflation.INF_DAT.length);
    }
    
    @Test
    public void testInfDatFirstValue() {
        assertEquals(1.0, UsInflation.INF_DAT[0], TOLERANCE);
    }
    
    @Test
    public void testInfDatLastValue() {
        assertEquals(4.1, UsInflation.INF_DAT[109], TOLERANCE);
    }
    
    @Test
    public void testInfAdjSameYear() {
        double value = 100.0;
        double result = UsInflation.infAdj(2020, 2020, value);
        assertEquals(100.0, result, TOLERANCE);
    }
    
    @Test
    public void testInfAdj2020to2023() {
        double value = 100.0;
        double result = UsInflation.infAdj(2020, 2023, value);
        assertTrue(result > 100.0);
    }
    
    @Test
    public void testInfAggregateSameYear() {
        double result = UsInflation.infAggregate(2020, 2020);
        assertEquals(0.0, result, TOLERANCE);
    }
    
    @Test
    public void testInfAggregate2020to2021() {
        double result = UsInflation.infAggregate(2020, 2021);
        assertEquals(4.7, result, TOLERANCE);
    }
    
    @Test
    public void testInfAggregateMultipleYears() {
        double result = UsInflation.infAggregate(2020, 2023);
        assertTrue(result > 0.0);
    }
    
    @Test
    public void testInfResampleReturnsValueFromArray() {
        double result = UsInflation.infResample(10);
        boolean found = false;
        for (int i = UsInflation.INF_DAT.length - 10; i < UsInflation.INF_DAT.length; i++) {
            if (Math.abs(result - UsInflation.INF_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled value should be from the last 10 years of data");
    }
    
    @Test
    public void testInfResampleFullHistory() {
        double result = UsInflation.infResample(110);
        boolean found = false;
        for (double value : UsInflation.INF_DAT) {
            if (Math.abs(result - value) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled value should be from the full data array");
    }
    
    @Test
    public void testInfAggregateInvalidYearRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            UsInflation.infAggregate(1900, 2020);
        });
    }
    
    @Test
    public void testInfAggregateReversedYears() {
        double result = UsInflation.infAggregate(2021, 2020);
        assertTrue(result < 0.0, "Reversed year range should give negative inflation");
    }
}
