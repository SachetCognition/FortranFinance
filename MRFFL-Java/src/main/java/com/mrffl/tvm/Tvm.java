package com.mrffl.tvm;

import com.mrffl.config.MrfflConfig;
import com.mrffl.config.VarSets;
import com.mrffl.util.Bitset;
import com.mrffl.util.Percentages;
import com.mrffl.solver.Solver;
import com.mrffl.solver.SolverResult;

import java.util.function.DoubleUnaryOperator;

public final class Tvm {
    
    private static final double CONSISTENT_EPSILON = 1.0e-3;
    
    public static final int ERROR_TOO_MANY_UNKNOWNS_LUMP = 1161;
    public static final int ERROR_UNKNOWN_UNKNOWNS_LUMP = 1162;
    public static final int ERROR_CANT_SOLVE_I_N_UNKNOWN_D_ZERO = 1163;
    public static final int ERROR_CANT_SOLVE_FV_N_UNKNOWN = 1164;
    public static final int ERROR_CANT_SOLVE_PV_I_UNKNOWN_D_EQ_N = 1165;
    public static final int ERROR_CANT_SOLVE_FV_I_UNKNOWN_D_ZERO = 1166;
    public static final int ERROR_INCONSISTENT_LUMP = 1167;
    
    public static final int ERROR_UNKNOWN_UNKNOWNS_LUMP_WRAPPER = 1193;
    public static final int ERROR_TOO_MANY_UNKNOWNS_LUMP_WRAPPER = 1194;
    
    private Tvm() {
        throw new AssertionError("Utility class");
    }
    
    /**
     * Compute future value from present value (pv), number of periods (n), and an interest rate (i).
     * 
     * @param pv Present value
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @return Future value
     */
    public static double fvFromPvNI(double pv, int n, double i) {
        return pv * Math.pow(1 + Percentages.percentageToFraction(i), n);
    }
    
    /**
     * Sum the payments from a geometric annuity.
     * The first non-zero payment is a, and the last non-zero payment is a*(1+g)^(n-1).
     * 
     * @param a First payment amount
     * @param g Growth rate as a percentage
     * @param n Number of payments
     * @return Sum of all payments
     */
    public static double tvmGeometricAnnuitySum(double a, double g, int n) {
        double gFrac = Percentages.percentageToFraction(g);
        if (Math.abs(gFrac) < MrfflConfig.ZERO_EPSILON) {
            return a * n;
        } else {
            return a * (Math.pow(1 + gFrac, n) - 1) / gFrac;
        }
    }
    
    /**
     * Compute the number of payments in a delayed annuity.
     * 
     * @param n Number of periods
     * @param d Delay (start boundary)
     * @param e Early end (end boundary)
     * @return Number of payments
     */
    public static int tvmDelayedAnnuityNumPayments(int n, int d, int e) {
        return 1 + n - e - d;
    }
    
    /**
     * Solve for TVM parameters for a lump sum.
     * This is a simple wrapper that calls tvmDelayedLumpSumSolve with d=0.
     * 
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param unknowns What variable to solve for
     * @return TvmResult with solved values and status
     */
    public static TvmResult tvmLumpSumSolve(double n, double i, double pv, double fv, int unknowns) {
        int allowedVars = VarSets.VAR_N + VarSets.VAR_I + VarSets.VAR_PV + VarSets.VAR_FV;
        
        if (Bitset.isNotSubset(unknowns, allowedVars)) {
            return new TvmResult(n, i, pv, fv, pv, 0, 0, ERROR_UNKNOWN_UNKNOWNS_LUMP_WRAPPER);
        }
        
        if (Bitset.size(unknowns) > 1) {
            return new TvmResult(n, i, pv, fv, pv, 0, 0, ERROR_TOO_MANY_UNKNOWNS_LUMP_WRAPPER);
        }
        
        double a = pv;
        if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
            return tvmDelayedLumpSumSolve(n, i, pv, fv, a, 0, VarSets.VAR_PV + VarSets.VAR_A);
        } else {
            return tvmDelayedLumpSumSolve(n, i, pv, fv, a, 0, unknowns);
        }
    }
    
    /**
     * Solve for TVM parameters for a generalized lump sum.
     * Can solve for any combination of 1 or 2 of: n, i, pv, fv, a.
     * 
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a Size of the cashflow
     * @param d Delay from time zero (0 = beginning of period 1, j = end of period j)
     * @param unknowns What variables to solve for
     * @return TvmResult with solved values and status
     */
    public static TvmResult tvmDelayedLumpSumSolve(double n, double i, double pv, double fv, double a, int d, int unknowns) {
        int allowedVars = VarSets.VAR_N + VarSets.VAR_I + VarSets.VAR_PV + VarSets.VAR_FV + VarSets.VAR_A;
        int numUnknowns = Bitset.size(unknowns);
        
        if (numUnknowns > 2) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_TOO_MANY_UNKNOWNS_LUMP);
        }
        
        if (Bitset.isNotSubset(unknowns, allowedVars)) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_UNKNOWN_UNKNOWNS_LUMP);
        }
        
        if (numUnknowns > 0) {
            double iq = Percentages.percentageToFraction(i);
            
            if (Bitset.isSubset(VarSets.VAR_A, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                    i = Percentages.fractionToPercentage(Math.pow(pv / fv, -1.0 / n) - 1);
                }
                iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = fv / Math.pow(1 + iq, n);
                } else if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = pv * Math.pow(1 + iq, n);
                } else if (Bitset.isSubset(VarSets.VAR_N, unknowns)) {
                    n = -Math.log(pv / fv) / Math.log(1 + iq);
                }
                a = fv * Math.pow(1 + iq, d - n);
                
            } else if (Bitset.isSubset(VarSets.VAR_N, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                    if (Math.abs(d) < MrfflConfig.ZERO_EPSILON) {
                        return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_CANT_SOLVE_I_N_UNKNOWN_D_ZERO);
                    }
                    i = Percentages.fractionToPercentage(Math.pow(pv / a, -1.0 / d) - 1);
                }
                iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = a * Math.pow(1 + iq, -d);
                } else if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_CANT_SOLVE_FV_N_UNKNOWN);
                }
                n = (d * Math.log(1 + iq) + Math.log(fv / a)) / Math.log(1 + iq);
                
            } else if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    if (Math.abs(d - n) < MrfflConfig.ZERO_EPSILON) {
                        return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_CANT_SOLVE_PV_I_UNKNOWN_D_EQ_N);
                    }
                    pv = a * Math.pow(Math.pow(fv / a, -1.0 / (d - n)), -d);
                } else if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    if (Math.abs(d) < MrfflConfig.ZERO_EPSILON) {
                        return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_CANT_SOLVE_FV_I_UNKNOWN_D_ZERO);
                    }
                    if (Math.abs(d - n) < MrfflConfig.ZERO_EPSILON) {
                        fv = a;
                    } else {
                        fv = Math.pow(pv / a, (d - n) / d) * a;
                    }
                }
                
                if (Math.abs(d - n) < MrfflConfig.ZERO_EPSILON) {
                    i = Percentages.fractionToPercentage(Math.exp(-Math.log(pv / a) / n) - 1);
                } else {
                    i = Percentages.fractionToPercentage(Math.exp(-Math.log(fv / a) / (d - n)) - 1);
                }
                
            } else {
                iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = a * Math.pow(1 + iq, -d + n);
                }
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = a * Math.pow(1 + iq, -d);
                }
            }
        }
        
        return tvmDelayedLumpSumCheck(n, i, pv, fv, a, d);
    }
    
    /**
     * Check consistency of TVM parameters for a lump sum.
     * 
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a Size of the cashflow
     * @param d Delay from time zero
     * @return TvmResult with status indicating consistency
     */
    public static TvmResult tvmDelayedLumpSumCheck(double n, double i, double pv, double fv, double a, int d) {
        double iq = Percentages.percentageToFraction(i);
        double expectedPv = a * Math.pow(1 + iq, -d);
        double expectedFv = a * Math.pow(1 + iq, n - d);
        
        if (Math.abs(pv - expectedPv) > CONSISTENT_EPSILON || Math.abs(fv - expectedFv) > CONSISTENT_EPSILON) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_INCONSISTENT_LUMP);
        }
        
        return new TvmResult(n, i, pv, fv, a, 0, 0, 0);
    }
}
