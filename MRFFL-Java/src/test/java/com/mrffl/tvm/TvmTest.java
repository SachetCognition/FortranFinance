package com.mrffl.tvm;

import com.mrffl.config.VarSets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TvmTest {
    
    private static final double TOLERANCE = 1.0e-6;
    
    @Test
    public void testFvFromPvNI() {
        double fv = Tvm.fvFromPvNI(1000, 3, 10);
        assertEquals(1331.0, fv, TOLERANCE);
    }
    
    @Test
    public void testTvmGeometricAnnuitySum() {
        double sum = Tvm.tvmGeometricAnnuitySum(100, 10, 5);
        assertEquals(610.51, sum, 0.01);
    }
    
    @Test
    public void testTvmGeometricAnnuitySumZeroGrowth() {
        double sum = Tvm.tvmGeometricAnnuitySum(100, 0, 5);
        assertEquals(500.0, sum, TOLERANCE);
    }
    
    @Test
    public void testTvmDelayedAnnuityNumPayments() {
        int numPayments = Tvm.tvmDelayedAnnuityNumPayments(5, 2, 1);
        assertEquals(3, numPayments);
    }
    
    @Test
    public void testTvmLumpSumSolvePv() {
        TvmResult result = Tvm.tvmLumpSumSolve(3, 10, -1, 1331, VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(1000.0, result.getPv(), TOLERANCE);
    }
    
    @Test
    public void testTvmLumpSumSolveFv() {
        TvmResult result = Tvm.tvmLumpSumSolve(3, 10, 1000, -1, VarSets.VAR_FV);
        assertTrue(result.isSuccess());
        assertEquals(1331.0, result.getFv(), TOLERANCE);
    }
    
    @Test
    public void testTvmLumpSumSolveI() {
        TvmResult result = Tvm.tvmLumpSumSolve(3, -1, 1000, 1331, VarSets.VAR_I);
        assertTrue(result.isSuccess());
        assertEquals(10.0, result.getI(), TOLERANCE);
    }
    
    @Test
    public void testTvmLumpSumSolveN() {
        TvmResult result = Tvm.tvmLumpSumSolve(-1, 10, 1000, 1331, VarSets.VAR_N);
        assertTrue(result.isSuccess());
        assertEquals(3.0, result.getN(), TOLERANCE);
    }
    
    @Test
    public void testTvmLumpSumSolveTooManyUnknowns() {
        TvmResult result = Tvm.tvmLumpSumSolve(-1, -1, 1000, 1331, VarSets.VAR_N + VarSets.VAR_I);
        assertFalse(result.isSuccess());
        assertEquals(Tvm.ERROR_TOO_MANY_UNKNOWNS_LUMP_WRAPPER, result.getStatus());
    }
    
    @Test
    public void testTvmDelayedLumpSumSolveAAndPv() {
        TvmResult result = Tvm.tvmDelayedLumpSumSolve(3, 10, -1, 1331, -1, 0, VarSets.VAR_A + VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(1000.0, result.getPv(), TOLERANCE);
        assertEquals(1000.0, result.getA(), TOLERANCE);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityTooManyUnknowns() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(-1, -1, -1, 1000, 1000, 0, 0, 
                                                            VarSets.VAR_N + VarSets.VAR_I + VarSets.VAR_FV);
        assertFalse(result.isSuccess());
        assertEquals(Tvm.ERROR_TOO_MANY_UNKNOWNS_LEVEL, result.getStatus());
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityUnknownVariable() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, 10, -1, -1, 1000, 0, 0, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm.ERROR_UNKNOWN_UNKNOWNS_LEVEL, result.getStatus());
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityOrdinaryAnnuity() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, 10, -1, 9487.1710000000130, 1000, 1, 0, VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(4868.4188176929347, result.getPv(), 0.01);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityDueAnnuity() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, 10, -1, 10435.888100000011, 1000, 0, 1, VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(5355.2606994622283, result.getPv(), 0.01);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuitySolveN() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(-1, 10, 4868.4188176929347, 9487.1710000000130, 1000, 1, 0, VarSets.VAR_N);
        assertTrue(result.isSuccess());
        assertEquals(7.0, result.getN(), TOLERANCE);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuitySolveI() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, -1, 4868.4188176929347, 9487.1710000000130, 1000, 1, 0, VarSets.VAR_I);
        assertTrue(result.isSuccess());
        assertEquals(10.0, result.getI(), 0.01);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuitySolveA() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, 10, 4868.4188176929347, 9487.1710000000130, -1, 1, 0, VarSets.VAR_A);
        assertTrue(result.isSuccess());
        assertEquals(1000.0, result.getA(), TOLERANCE);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityDelayedPayment() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, 10, -1, 10435.888100000011, 1000, 0, 1, VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(5355.2606994622283, result.getPv(), 0.01);
    }
    
    @Test
    public void testTvmDelayedLevelAnnuityNegativeInterest() {
        TvmResult result = Tvm.tvmDelayedLevelAnnuitySolve(7, -10, -1, 1385.1, 1000, 3, 3, VarSets.VAR_PV);
        assertTrue(result.isSuccess());
        assertEquals(2895.9, result.getPv(), 1.0);
    }
}
