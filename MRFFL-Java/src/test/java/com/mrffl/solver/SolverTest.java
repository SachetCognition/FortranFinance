package com.mrffl.solver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Solver root-finding methods.
 */
public class SolverTest {
    
    private static final double TOLERANCE = 1e-6;
    
    @Test
    public void testBisectionLinearFunction() {
        SolverResult result = Solver.bisection(
            0.0, 10.0,
            x -> x - 5.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(Solver.SUCCESS, result.getStatus());
        assertEquals(5.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testBisectionQuadraticFunction() {
        SolverResult result = Solver.bisection(
            0.0, 5.0,
            x -> x * x - 4.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(2.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testBisectionNegativeRoot() {
        SolverResult result = Solver.bisection(
            -5.0, 0.0,
            x -> x * x - 4.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(-2.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testBisectionCubicFunction() {
        SolverResult result = Solver.bisection(
            0.0, 2.0,
            x -> x * x * x - x - 1.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(1.3247179572, result.getXc(), 1e-6);
    }
    
    @Test
    public void testBisectionSameSign() {
        SolverResult result = Solver.bisection(
            1.0, 3.0,
            x -> x * x,
            1e-8, 1e-8, 100, false
        );
        
        assertFalse(result.isSuccess());
        assertEquals(Solver.ERROR_SAME_SIGN, result.getStatus());
    }
    
    @Test
    public void testBisectionWithVeryTightTolerance() {
        SolverResult result = Solver.bisection(
            0.0, 3.0,
            x -> x * x - 2.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(Math.sqrt(2.0), result.getXc(), 1e-6);
    }
    
    @Test
    public void testBisectionFailedToConverge() {
        SolverResult result = Solver.bisection(
            0.0, 2.0,
            x -> x * x - 2.0,
            1e-15, 1e-15, 3, false
        );
        
        assertEquals(Solver.ERROR_FAILED_TO_CONVERGE, result.getStatus());
        assertEquals(3, result.getIterations());
    }
    
    @Test
    public void testBisectionWithProgress() {
        SolverResult result = Solver.bisection(
            0.0, 10.0,
            x -> x - 5.0,
            1e-6, 1e-6, 100, true
        );
        
        assertTrue(result.isSuccess());
        assertTrue(result.getIterations() > 0);
    }
    
    @Test
    public void testMultiBisectionFirstInterval() {
        double[] x0s = {0.0, 10.0, 20.0};
        double[] x1s = {5.0, 15.0, 25.0};
        
        SolverResult result = Solver.multiBisection(
            x0s, x1s,
            x -> x - 2.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(2.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testMultiBisectionSecondInterval() {
        double[] x0s = {0.0, 10.0, 20.0};
        double[] x1s = {5.0, 15.0, 25.0};
        
        SolverResult result = Solver.multiBisection(
            x0s, x1s,
            x -> x - 12.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(12.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testMultiBisectionThirdInterval() {
        double[] x0s = {0.0, 10.0, 20.0};
        double[] x1s = {5.0, 15.0, 25.0};
        
        SolverResult result = Solver.multiBisection(
            x0s, x1s,
            x -> x - 22.0,
            1e-8, 1e-8, 100, false
        );
        
        assertTrue(result.isSuccess());
        assertEquals(22.0, result.getXc(), TOLERANCE);
    }
    
    @Test
    public void testMultiBisectionNoRoot() {
        double[] x0s = {0.0, 10.0, 20.0};
        double[] x1s = {5.0, 15.0, 25.0};
        
        SolverResult result = Solver.multiBisection(
            x0s, x1s,
            x -> x - 100.0,
            1e-8, 1e-8, 100, false
        );
        
        assertFalse(result.isSuccess());
        assertEquals(Solver.ERROR_NO_ROOT_FOUND, result.getStatus());
    }
    
    @Test
    public void testMultiBisectionMismatchedArrays() {
        double[] x0s = {0.0, 10.0};
        double[] x1s = {5.0, 15.0, 25.0};
        
        assertThrows(IllegalArgumentException.class, () -> {
            Solver.multiBisection(x0s, x1s, x -> x, 1e-8, 1e-8, 100, false);
        });
    }
    
    @Test
    public void testSolverResultToString() {
        SolverResult result = new SolverResult(5.0, Solver.SUCCESS, 10);
        String str = result.toString();
        assertTrue(str.contains("5.0"));
        assertTrue(str.contains("0"));
        assertTrue(str.contains("10"));
    }
}
