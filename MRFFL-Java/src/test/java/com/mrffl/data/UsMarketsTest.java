package com.mrffl.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsMarketsTest {
    
    private static final double TOLERANCE = 1e-6;
    
    @Test
    public void testSnpDatArraySize() {
        assertEquals(54, UsMarkets.SNP_DAT.length);
    }
    
    @Test
    public void testRutDatArraySize() {
        assertEquals(36, UsMarkets.RUT_DAT.length);
    }
    
    @Test
    public void testNasDatArraySize() {
        assertEquals(50, UsMarkets.NAS_DAT.length);
    }
    
    @Test
    public void testDowDatArraySize() {
        assertEquals(54, UsMarkets.DOW_DAT.length);
    }
    
    @Test
    public void testDgs10DatArraySize() {
        assertEquals(64, UsMarkets.DGS10_DAT.length);
    }
    
    @Test
    public void testSnpResampleReturnsValueFromArray() {
        double result = UsMarkets.snpResample(10);
        boolean found = false;
        for (int i = UsMarkets.SNP_DAT.length - 10; i < UsMarkets.SNP_DAT.length; i++) {
            if (Math.abs(result - UsMarkets.SNP_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled SNP value should be from the last 10 years");
    }
    
    @Test
    public void testRutResampleReturnsValueFromArray() {
        double result = UsMarkets.rutResample(5);
        boolean found = false;
        for (int i = UsMarkets.RUT_DAT.length - 5; i < UsMarkets.RUT_DAT.length; i++) {
            if (Math.abs(result - UsMarkets.RUT_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled RUT value should be from the last 5 years");
    }
    
    @Test
    public void testNasResampleReturnsValueFromArray() {
        double result = UsMarkets.nasResample(15);
        boolean found = false;
        for (int i = UsMarkets.NAS_DAT.length - 15; i < UsMarkets.NAS_DAT.length; i++) {
            if (Math.abs(result - UsMarkets.NAS_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled NAS value should be from the last 15 years");
    }
    
    @Test
    public void testDowResampleReturnsValueFromArray() {
        double result = UsMarkets.dowResample(20);
        boolean found = false;
        for (int i = UsMarkets.DOW_DAT.length - 20; i < UsMarkets.DOW_DAT.length; i++) {
            if (Math.abs(result - UsMarkets.DOW_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled DOW value should be from the last 20 years");
    }
    
    @Test
    public void testDgs10ResampleReturnsValueFromArray() {
        double result = UsMarkets.dgs10Resample(30);
        boolean found = false;
        for (int i = UsMarkets.DGS10_DAT.length - 30; i < UsMarkets.DGS10_DAT.length; i++) {
            if (Math.abs(result - UsMarkets.DGS10_DAT[i]) < TOLERANCE) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Resampled DGS10 value should be from the last 30 years");
    }
    
    @Test
    public void testSnpResampleRandomness() {
        double first = UsMarkets.snpResample(54);
        boolean foundDifferent = false;
        for (int i = 0; i < 100; i++) {
            double next = UsMarkets.snpResample(54);
            if (Math.abs(first - next) > TOLERANCE) {
                foundDifferent = true;
                break;
            }
        }
        assertTrue(foundDifferent, "Multiple resamples should produce different values");
    }
    
    @Test
    public void testAllResampleFunctionsWork() {
        assertDoesNotThrow(() -> {
            UsMarkets.snpResample(10);
            UsMarkets.rutResample(10);
            UsMarkets.nasResample(10);
            UsMarkets.dowResample(10);
            UsMarkets.dgs10Resample(10);
        });
    }
}
