package com.mrffl.solver;

/**
 * Result from a bisection solver operation.
 * 
 * Contains the solution value (or last tested value if failed),
 * status code indicating success or failure reason, and the
 * number of iterations performed.
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public class SolverResult {
    
    private final double xc;
    private final int status;
    private final int iterations;
    
    /**
     * Create a solver result.
     * 
     * @param xc the solution value (or last tested value)
     * @param status the status code (0 for success, error codes otherwise)
     * @param iterations the number of iterations performed
     */
    public SolverResult(double xc, int status, int iterations) {
        this.xc = xc;
        this.status = status;
        this.iterations = iterations;
    }
    
    /**
     * Get the solution value or last tested value.
     * 
     * @return the x coordinate where the function crosses zero (or closest point)
     */
    public double getXc() {
        return xc;
    }
    
    /**
     * Get the status code.
     * 
     * @return 0 for success, error code otherwise
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Get the number of iterations performed.
     * 
     * @return iteration count
     */
    public int getIterations() {
        return iterations;
    }
    
    /**
     * Check if the solver succeeded.
     * 
     * @return true if status is SUCCESS (0)
     */
    public boolean isSuccess() {
        return status == Solver.SUCCESS;
    }
    
    @Override
    public String toString() {
        return String.format("SolverResult{xc=%.6f, status=%d, iterations=%d}", 
                           xc, status, iterations);
    }
}
