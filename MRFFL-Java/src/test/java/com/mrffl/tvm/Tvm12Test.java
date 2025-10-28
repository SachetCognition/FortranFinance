package com.mrffl.tvm;

import com.mrffl.config.VarSets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Tvm12Test {
    
    private static final double TOLERANCE = 1e-4;
    
    @Test
    public void testSolveForPmtEnd() {
        int n = 3;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -1.0;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_END, VarSets.VAR_PMT);
        
        assertTrue(result.isSuccess());
        assertEquals(-392.38339254851439, result.getPmt(), TOLERANCE);
    }
    
    @Test
    public void testSolveForNEnd() {
        int n = -1;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -392.38339254851439;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_END, VarSets.VAR_N);
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getN());
    }
    
    @Test
    public void testSolveForIEnd() {
        int n = 3;
        double i = -1.0;
        double pv = 1000.0;
        double pmt = -392.38339254851439;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_END, VarSets.VAR_I);
        
        assertTrue(result.isSuccess());
        assertEquals(0.04, result.getI(), TOLERANCE);
    }
    
    @Test
    public void testSolveForPvEnd() {
        int n = 3;
        double i = 0.04;
        double pv = -1.0;
        double pmt = -392.38339254851439;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_END, VarSets.VAR_PV);
        
        assertTrue(result.isSuccess());
        assertEquals(1000.0, result.getPv(), TOLERANCE);
    }
    
    @Test
    public void testSolveForFvEnd() {
        int n = 3;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -392.38339254851439;
        double fv = -1.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_END, VarSets.VAR_FV);
        
        assertTrue(result.isSuccess());
        assertEquals(100.0, result.getFv(), TOLERANCE);
    }
    
    @Test
    public void testSolveForPmtBeginning() {
        int n = 3;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -1.0;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_BEGINNING, VarSets.VAR_PMT);
        
        assertTrue(result.isSuccess());
        assertEquals(-377.29172392869191, result.getPmt(), TOLERANCE);
    }
    
    @Test
    public void testSolveForNBeginning() {
        int n = -1;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -377.29172392869191;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_BEGINNING, VarSets.VAR_N);
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getN());
    }
    
    @Test
    public void testSolveForIBeginning() {
        int n = 3;
        double i = -1.0;
        double pv = 1000.0;
        double pmt = -377.29172392869191;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_BEGINNING, VarSets.VAR_I);
        
        assertTrue(result.isSuccess());
        assertEquals(0.04, result.getI(), TOLERANCE);
    }
    
    @Test
    public void testSolveForPvBeginning() {
        int n = 3;
        double i = 0.04;
        double pv = -1.0;
        double pmt = -377.29172392869191;
        double fv = 100.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_BEGINNING, VarSets.VAR_PV);
        
        assertTrue(result.isSuccess());
        assertEquals(1000.0, result.getPv(), TOLERANCE);
    }
    
    @Test
    public void testSolveForFvBeginning() {
        int n = 3;
        double i = 0.04;
        double pv = 1000.0;
        double pmt = -377.29172392869191;
        double fv = -1.0;
        
        Tvm12Result result = Tvm12.tvm12Solve(n, i, pv, pmt, fv, Tvm12.PMT_AT_BEGINNING, VarSets.VAR_FV);
        
        assertTrue(result.isSuccess());
        assertEquals(100.0, result.getFv(), TOLERANCE);
    }
    
    @Test
    public void testErrorNZero() {
        Tvm12Result result = Tvm12.tvm12Solve(0, 0.04, 1000.0, -392.38, 100.0, 
                                               Tvm12.PMT_AT_END, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm12.ERROR_N_ZERO, result.getStatus());
    }
    
    @Test
    public void testErrorNNegative() {
        Tvm12Result result = Tvm12.tvm12Solve(-5, 0.04, 1000.0, -392.38, 100.0, 
                                               Tvm12.PMT_AT_END, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm12.ERROR_N_NEGATIVE, result.getStatus());
    }
    
    @Test
    public void testErrorINearZero() {
        Tvm12Result result = Tvm12.tvm12Solve(3, 0.0, 1000.0, -392.38, 100.0, 
                                               Tvm12.PMT_AT_END, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm12.ERROR_I_NEAR_ZERO, result.getStatus());
    }
    
    @Test
    public void testErrorINearMinusOne() {
        Tvm12Result result = Tvm12.tvm12Solve(3, -1.0, 1000.0, -392.38, 100.0, 
                                               Tvm12.PMT_AT_END, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm12.ERROR_I_NEAR_MINUS_ONE, result.getStatus());
    }
    
    @Test
    public void testErrorUnsupportedPmtTime() {
        Tvm12Result result = Tvm12.tvm12Solve(3, 0.04, 1000.0, -392.38, 100.0, 
                                               999, VarSets.VAR_PMT);
        assertFalse(result.isSuccess());
        assertEquals(Tvm12.ERROR_UNSUPPORTED_PMT_TIME, result.getStatus());
    }
}
