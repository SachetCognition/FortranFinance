package com.mrffl.solver;

import com.mrffl.config.MrfflConfig;
import java.util.function.DoubleUnaryOperator;

/**
 * Root-finding algorithms using the bisection method.
 * 
 * The bisection method is a robust numerical technique for finding roots
 * (zeros) of continuous functions. It works by repeatedly bisecting an
 * interval and selecting subintervals where a sign change occurs.
 * 
 * Corresponds to MRFFL/src/mrffl_solver.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class Solver {
    
    public static final int SUCCESS = 0;
    public static final int ERROR_SAME_SIGN = 4001;
    public static final int ERROR_INTERVAL_TOO_SMALL = 4002;
    public static final int ERROR_FAILED_TO_CONVERGE = 4003;
    public static final int ERROR_NO_ROOT_FOUND = 4033;
    
    /**
     * Find a root of a function using the bisection method.
     * 
     * The bisection method searches for a value xc in the interval [x0_init, x1_init]
     * where f(xc) ≈ 0. The method requires that f(x0_init) and f(x1_init) have
     * opposite signs, ensuring a root exists in the interval by the Intermediate
     * Value Theorem.
     * 
     * @param x0Init initial lower bound of search interval
     * @param x1Init initial upper bound of search interval
     * @param f the function to solve (must be continuous in the interval)
     * @param xEpsilon tolerance for search interval width
     * @param yEpsilon tolerance for function value (how close to zero)
     * @param maxItr maximum number of iterations before giving up
     * @param progress if true, print iteration details to stdout
     * @return SolverResult containing solution, status code, and iteration count
     */
    public static SolverResult bisection(double x0Init, double x1Init, 
                                        DoubleUnaryOperator f,
                                        double xEpsilon, double yEpsilon,
                                        int maxItr, boolean progress) {
        double x0 = x0Init;
        double x1 = x1Init;
        double y0 = f.applyAsDouble(x0);
        double y1 = f.applyAsDouble(x1);
        
        if (progress) {
            System.out.printf("bisection: x0=%.6f y0=%.6f x1=%.6f y1=%.6f%n", 
                            x0, y0, x1, y1);
        }
        
        if (y0 * y1 > 0) {
            return new SolverResult(x0, ERROR_SAME_SIGN, 0);
        }
        
        int itr = 0;
        while (itr < maxItr) {
            itr++;
            
            double xc = (x0 + x1) / 2.0;
            double yc = f.applyAsDouble(xc);
            
            if (progress) {
                System.out.printf("  itr=%d xc=%.6f yc=%.6f%n", itr, xc, yc);
            }
            
            if (Math.abs(yc) < yEpsilon) {
                return new SolverResult(xc, SUCCESS, itr);
            }
            
            if (Math.abs(x1 - x0) < xEpsilon) {
                return new SolverResult(xc, SUCCESS, itr);
            }
            
            if (y0 * yc < 0) {
                x1 = xc;
                y1 = yc;
            } else {
                x0 = xc;
                y0 = yc;
            }
        }
        
        double xc = (x0 + x1) / 2.0;
        return new SolverResult(xc, ERROR_FAILED_TO_CONVERGE, itr);
    }
    
    /**
     * Find a root across multiple intervals using the bisection method.
     * 
     * This method attempts bisection on multiple search intervals in sequence.
     * It returns the first root found, or an error if no root exists in any
     * of the provided intervals.
     * 
     * @param x0Inits array of lower bounds for each search interval
     * @param x1Inits array of upper bounds for each search interval
     * @param f the function to solve (must be continuous in the intervals)
     * @param xEpsilon tolerance for search interval width
     * @param yEpsilon tolerance for function value (how close to zero)
     * @param maxItr maximum number of iterations per interval before giving up
     * @param progress if true, print iteration details to stdout
     * @return SolverResult containing solution, status code, and iteration count
     */
    public static SolverResult multiBisection(double[] x0Inits, double[] x1Inits,
                                             DoubleUnaryOperator f,
                                             double xEpsilon, double yEpsilon,
                                             int maxItr, boolean progress) {
        if (x0Inits.length != x1Inits.length) {
            throw new IllegalArgumentException(
                "x0Inits and x1Inits must have the same length");
        }
        
        int totalIterations = 0;
        
        for (int i = 0; i < x0Inits.length; i++) {
            SolverResult result = bisection(x0Inits[i], x1Inits[i], f, 
                                           xEpsilon, yEpsilon, maxItr, progress);
            totalIterations += result.getIterations();
            
            if (result.getStatus() == SUCCESS) {
                return new SolverResult(result.getXc(), SUCCESS, totalIterations);
            }
        }
        
        return new SolverResult(0.0, ERROR_NO_ROOT_FOUND, totalIterations);
    }
    
    private Solver() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
