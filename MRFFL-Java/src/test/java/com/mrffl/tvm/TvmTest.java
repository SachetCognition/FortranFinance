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
}
