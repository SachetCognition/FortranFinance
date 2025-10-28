package com.mrffl.cashflow;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CashflowsTest {
    
    private static final double TOLERANCE = 1e-4;
    
    @Test
    public void testCashflowVectorTotalPv() {
        double[] cf = {0, 100, 200, 300, 400, 500};
        double i = 4.0;
        double totalPv = Cashflows.cashflowVectorTotalPv(cf, i);
        assertTrue(totalPv > 0);
        assertTrue(totalPv < 1500);
    }
    
    @Test
    public void testCashflowVectorTotalPvComplex() {
        double[] cf = {-80000, -500, 4500, 5500, 4500, 130000};
        double i = 4.0;
        double totalPv = Cashflows.cashflowVectorTotalPv(cf, i);
        assertTrue(totalPv > 0);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedLump() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedLump(5, 100.0, 3);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(100.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedLumpErrors() {
        CashflowVectorResult result1 = Cashflows.makeCashflowVectorDelayedLump(0, 100.0, 3);
        assertFalse(result1.isSuccess());
        assertEquals(Cashflows.ERROR_CF_LUMP_N_LT_1, result1.getStatus());
        
        CashflowVectorResult result2 = Cashflows.makeCashflowVectorDelayedLump(5, 100.0, -1);
        assertFalse(result2.isSuccess());
        assertEquals(Cashflows.ERROR_CF_LUMP_D_LT_0, result2.getStatus());
        
        CashflowVectorResult result3 = Cashflows.makeCashflowVectorDelayedLump(5, 100.0, 10);
        assertFalse(result3.isSuccess());
        assertEquals(Cashflows.ERROR_CF_LUMP_D_GT_N, result3.getStatus());
    }
    
    @Test
    public void testMakeCashflowVectorDelayedLevelAnnuity() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedLevelAnnuity(5, 100.0, 3, 1);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(0.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedLevelAnnuityNoDelay() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedLevelAnnuity(5, 100.0, 0, 1);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(100.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedGeometricAnnuity() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedGeometricAnnuity(5, 10.0, 100.0, 0, 1);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(100.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(110.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(121.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(133.1, result.getCfVec()[3], TOLERANCE);
        assertEquals(146.41, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedGeometricAnnuityWithDelay() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedGeometricAnnuity(5, 10.0, 100.0, 3, 1);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(0.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(110.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedArithmeticAnnuity() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedArithmeticAnnuity(5, 100.0, 100.0, 1, 0);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(0.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(100.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(200.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(300.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(400.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(500.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testMakeCashflowVectorDelayedArithmeticAnnuityNoDelay() {
        CashflowVectorResult result = Cashflows.makeCashflowVectorDelayedArithmeticAnnuity(5, 100.0, 100.0, 0, 1);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getCfVec().length);
        assertEquals(100.0, result.getCfVec()[0], TOLERANCE);
        assertEquals(200.0, result.getCfVec()[1], TOLERANCE);
        assertEquals(300.0, result.getCfVec()[2], TOLERANCE);
        assertEquals(400.0, result.getCfVec()[3], TOLERANCE);
        assertEquals(500.0, result.getCfVec()[4], TOLERANCE);
        assertEquals(0.0, result.getCfVec()[5], TOLERANCE);
    }
    
    @Test
    public void testCashflowMatrixPvFv() {
        double[][] cfMat = {
            {-80000, 0, 100, 100, 0, 0, 0},
            {-500, 0, 200, 200, 0, 0, 0},
            {4500, 0, 300, 300, 0, 100, 0},
            {5500, 0, 400, 400, 100, 100, 0},
            {4500, 0, 500, 500, 0, 0, 0},
            {130000, 0, 0, 0, 0, 0, 100}
        };
        double i = 4.0;
        
        CashflowPvFvResult result = Cashflows.cashflowMatrixPvFv(cfMat, i);
        assertTrue(result.isSuccess());
        assertEquals(6, result.getPvVec().length);
        assertEquals(6, result.getFvVec().length);
    }
    
    @Test
    public void testCashflowMatrixPvFvErrors() {
        double[][] emptyMat = new double[5][0];
        CashflowPvFvResult result1 = Cashflows.cashflowMatrixPvFv(emptyMat, 4.0);
        assertFalse(result1.isSuccess());
        assertEquals(Cashflows.ERROR_CF_MATRIX_NO_FLOWS, result1.getStatus());
        
        double[][] normalMat = {{100}, {200}};
        CashflowPvFvResult result2 = Cashflows.cashflowMatrixPvFv(normalMat, -100.0);
        assertFalse(result2.isSuccess());
        assertEquals(Cashflows.ERROR_CF_MATRIX_I_NEAR_MINUS_100, result2.getStatus());
        
        double[][] smallMat = {{100}};
        CashflowPvFvResult result3 = Cashflows.cashflowMatrixPvFv(smallMat, 4.0);
        assertFalse(result3.isSuccess());
        assertEquals(Cashflows.ERROR_CF_MATRIX_N_TOO_SMALL, result3.getStatus());
    }
    
    @Test
    public void testCashflowVectorIrr() {
        double[] cf = {-1000, 300, 300, 300, 300, 300};
        CashflowIrrResult result = Cashflows.cashflowVectorIrr(cf, 0.0);
        assertTrue(result.isSuccess());
        assertTrue(result.getIrr() > 0);
        assertTrue(result.getIrr() < 20);
    }
    
    @Test
    public void testCashflowMatrixIrr() {
        double[][] cfMat = {
            {-1000, -2000},
            {300, 600},
            {300, 600},
            {300, 600},
            {300, 600},
            {300, 600}
        };
        CashflowIrrResult result = Cashflows.cashflowMatrixIrr(cfMat, 0.0);
        assertTrue(result.isSuccess());
        assertTrue(result.getIrr() > 0);
    }
    
    @Test
    public void testAddInterestToCashflowVector() {
        double[] cf = {100, 0, 0, 0, 0, 0};
        int status = Cashflows.addInterestToCashflowVector(cf, 10.0);
        assertEquals(Cashflows.SUCCESS, status);
        assertEquals(100.0, cf[0], TOLERANCE);
        assertEquals(10.0, cf[1], TOLERANCE);
        assertEquals(11.0, cf[2], TOLERANCE);
        assertTrue(cf[3] > 12.0 && cf[3] < 13.0);
    }
    
    @Test
    public void testAddMultiInterestToCashflowVector() {
        double[] cf = {100, 0, 0, 0, 0, 0};
        double[] vrate = {10.0, 10.0, 10.0, 10.0, 10.0};
        int status = Cashflows.addMultiInterestToCashflowVector(cf, vrate);
        assertEquals(Cashflows.SUCCESS, status);
        assertEquals(100.0, cf[0], TOLERANCE);
        assertEquals(10.0, cf[1], TOLERANCE);
        assertEquals(11.0, cf[2], TOLERANCE);
    }
    
    @Test
    public void testAddMultiInterestToCashflowVectorError() {
        double[] cf = {100, 0, 0, 0, 0, 0};
        double[] vrate = {10.0, 10.0};
        int status = Cashflows.addMultiInterestToCashflowVector(cf, vrate);
        assertEquals(4065, status);
    }
    
    @Test
    public void testCashflowMatrixTotalPv() {
        double[][] cfMat = {
            {100, 200},
            {200, 300},
            {300, 400}
        };
        double i = 4.0;
        double totalPv = Cashflows.cashflowMatrixTotalPv(cfMat, i);
        assertTrue(totalPv > 0);
        assertTrue(totalPv < 1500);
    }
}
