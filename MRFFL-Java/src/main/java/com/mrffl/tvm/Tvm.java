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
    
    public static final int ERROR_TOO_MANY_UNKNOWNS_LEVEL = 1097;
    public static final int ERROR_UNKNOWN_UNKNOWNS_LEVEL = 1098;
    public static final int ERROR_LEVEL_I_SOLVER_FAILED_NO_N = 1099;
    public static final int ERROR_LEVEL_I_SOLVER_FAILED_NO_FV = 1100;
    public static final int ERROR_LEVEL_I_SOLVER_FAILED_NO_PV = 1101;
    public static final int ERROR_INCONSISTENT_LEVEL = 1102;
    
    public static final int ERROR_TOO_MANY_UNKNOWNS_GEOMETRIC = 1033;
    public static final int ERROR_UNKNOWN_UNKNOWNS_GEOMETRIC = 1034;
    public static final int ERROR_GEOMETRIC_N_SOLVER_FAILED = 1035;
    public static final int ERROR_GEOMETRIC_N_FV_SOLVER_FAILED = 1036;
    public static final int ERROR_GEOMETRIC_PV_FV_SOLVER_FAILED = 1037;
    public static final int ERROR_GEOMETRIC_A_SOLVER_FAILED = 1038;
    public static final int ERROR_INCONSISTENT_GEOMETRIC = 1001;
    
    public static final int ERROR_ARITHMETIC_UNABLE_TO_SOLVE = 4129;
    public static final int ERROR_INCONSISTENT_ARITHMETIC = 4097;
    
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
    
    /**
     * Solve for TVM parameters for a level annuity certain.
     * Can solve for any combination of 1 or 2 of: n, i, pv, fv, a.
     * 
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero
     * @param e Early end counted from time end (t=n)
     * @param unknowns What variables to solve for
     * @return TvmResult with solved values and status
     */
    public static TvmResult tvmDelayedLevelAnnuitySolve(double n, double i, double pv, double fv, double a, int d, int e, int unknowns) {
        int allowedVars = VarSets.VAR_N + VarSets.VAR_I + VarSets.VAR_PV + VarSets.VAR_FV + VarSets.VAR_A;
        int numUnknowns = Bitset.size(unknowns);
        
        if (numUnknowns > 2) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_TOO_MANY_UNKNOWNS_LEVEL);
        }
        
        if (Bitset.isNotSubset(unknowns, allowedVars)) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_UNKNOWN_UNKNOWNS_LEVEL);
        }
        
        if (numUnknowns > 0) {
            double[] islvivl0 = {0.0 + MrfflConfig.ZERO_EPSILON, -100.0 + MrfflConfig.ZERO_EPSILON, -99999.0};
            double[] islvivl1 = {99999.0, 0.0 - MrfflConfig.ZERO_EPSILON, -100.0 - MrfflConfig.ZERO_EPSILON};
            
            if (Bitset.isSubset(VarSets.VAR_A, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                    i = Percentages.fractionToPercentage(Math.pow(fv / pv, 1.0 / n) - 1);
                }
                double iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = Math.pow(1 + iq, -n) * fv;
                } else if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = pv * Math.pow(1 + iq, n);
                } else if (Bitset.isSubset(VarSets.VAR_N, unknowns)) {
                    n = -Math.log(pv / fv) / Math.log(1 + iq);
                }
                a = -fv * iq / (Math.pow(1 + iq, e) - Math.pow(1 + iq, 1 + n - d));
                
            } else if (Bitset.isSubset(VarSets.VAR_N, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                    if ((d == 0) && (e == 0)) {
                        i = Percentages.fractionToPercentage(-(fv - pv) * a / (a - pv) / fv);
                    } else if ((d == 0) && (e == 1)) {
                        i = Percentages.fractionToPercentage(-(fv - pv) * a / (a * fv - a * pv - fv * pv));
                    } else if ((d == 1) && (e == 0)) {
                        i = Percentages.fractionToPercentage(a * (fv - pv) / fv / pv);
                    } else if ((d == 1) && (e == 1)) {
                        i = Percentages.fractionToPercentage((fv - pv) * a / pv / (a + fv));
                    } else {
                        final double nFinal = n;
                        final double pvFinal = pv;
                        final double fvFinal = fv;
                        final double aFinal = a;
                        final int dFinal = d;
                        final int eFinal = e;
                        
                        DoubleUnaryOperator sfINoN = iVal -> {
                            double iq = Percentages.percentageToFraction(iVal);
                            return (aFinal * Math.pow(1 + iq, eFinal) + fvFinal * iq) * pvFinal - Math.pow(1 + iq, 1 - dFinal) * aFinal * fvFinal;
                        };
                        
                        SolverResult result = Solver.multiBisection(islvivl0, islvivl1, sfINoN, 1.0e-5, 1.0e-5, 1000, false);
                        if (result.getStatus() != 0) {
                            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_LEVEL_I_SOLVER_FAILED_NO_N);
                        }
                        i = result.getXc();
                    }
                }
                double iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = Math.pow(1 + iq, 1 - d) * a * fv / (a * Math.pow(1 + iq, e) + fv * iq);
                } else if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = Math.pow(1 + iq, e) * a * pv / (Math.pow(1 + iq, 1 - d) * a - iq * pv);
                }
                n = (Math.log(1 + iq) * d - Math.log(1 + iq) + Math.log((a * Math.exp(Math.log(1 + iq) * e) + fv * iq) / a)) / Math.log(1 + iq);
                
            } else {
                if (Bitset.isSubset(VarSets.VAR_I, unknowns)) {
                    if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                        final double nFinal = n;
                        final double pvFinal = pv;
                        final double aFinal = a;
                        final int dFinal = d;
                        final int eFinal = e;
                        
                        DoubleUnaryOperator sfINoFv = iVal -> {
                            double iq = Percentages.percentageToFraction(iVal);
                            return (-Math.pow(1.0 / (1 + iq), nFinal - eFinal + 1) * (1 + iq) / iq + Math.pow(1.0 / (1 + iq), dFinal) * (1 + iq) / iq) * aFinal - pvFinal;
                        };
                        
                        SolverResult result = Solver.multiBisection(islvivl0, islvivl1, sfINoFv, 1.0e-7, 1.0e-7, 1000, false);
                        if (result.getStatus() != 0) {
                            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_LEVEL_I_SOLVER_FAILED_NO_FV);
                        }
                        i = result.getXc();
                        
                    } else if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                        final double nFinal = n;
                        final double fvFinal = fv;
                        final double aFinal = a;
                        final int dFinal = d;
                        final int eFinal = e;
                        
                        DoubleUnaryOperator sfINoPv = iVal -> {
                            double iq = Percentages.percentageToFraction(iVal);
                            return 1.0 / iq * (-Math.pow(1 + iq, eFinal) + Math.pow(1 + iq, 1 + nFinal - dFinal)) * aFinal - fvFinal;
                        };
                        
                        SolverResult result = Solver.multiBisection(islvivl0, islvivl1, sfINoPv, 1.0e-7, 1.0e-7, 1000, false);
                        if (result.getStatus() != 0) {
                            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_LEVEL_I_SOLVER_FAILED_NO_PV);
                        }
                        i = result.getXc();
                        
                    } else {
                        i = Percentages.fractionToPercentage(Math.pow(fv / pv, 1.0 / n) - 1);
                    }
                }
                double iq = Percentages.percentageToFraction(i);
                
                if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = 1.0 / iq * (-Math.pow(1 + iq, e) + Math.pow(1 + iq, 1 + n - d)) * a;
                }
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = (-Math.pow(1.0 / (1 + iq), n - e + 1) * (1 + iq) / iq + Math.pow(1.0 / (1 + iq), d) * (1 + iq) / iq) * a;
                }
            }
        }
        
        return tvmDelayedLevelAnnuityCheck(n, i, pv, fv, a, d, e);
    }
    
    /**
     * Check consistency of TVM parameters for a level annuity.
     * 
     * @param n Number of compounding periods
     * @param i Annual growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero
     * @param e Early end counted from time end
     * @return TvmResult with status indicating consistency
     */
    public static TvmResult tvmDelayedLevelAnnuityCheck(double n, double i, double pv, double fv, double a, int d, int e) {
        double iq = Percentages.percentageToFraction(i);
        
        double expectedFv = 1.0 / iq * (-Math.pow(1 + iq, e) + Math.pow(1 + iq, 1 + n - d)) * a;
        double expectedPv = (-Math.pow(1.0 / (1 + iq), n - e + 1) * (1 + iq) / iq + Math.pow(1.0 / (1 + iq), d) * (1 + iq) / iq) * a;
        
        if (Math.abs(expectedFv - fv) > CONSISTENT_EPSILON) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_INCONSISTENT_LEVEL);
        }
        if (Math.abs(expectedPv - pv) > CONSISTENT_EPSILON) {
            return new TvmResult(n, i, pv, fv, a, 0, 0, ERROR_INCONSISTENT_LEVEL);
        }
        
        return new TvmResult(n, i, pv, fv, a, 0, 0, 0);
    }
    
    /**
     * Solve for TVM parameters for a geometric annuity certain.
     * Can solve one or two variables from {var_n, var_pv, var_fv, var_a}.
     * 
     * @param n Number of compounding periods
     * @param i Discount rate as a percentage
     * @param g Payment growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero (d=0 is beginning of period 1, d=j is end of period j)
     * @param e Early end counted from time end (e=0 means last payment at end of period n)
     * @param unknowns Variables to solve for
     * @return TvmResult with status indicating success (0) or error code
     */
    public static TvmResult tvmDelayedGeometricAnnuitySolve(double n, double i, double g, double pv, double fv, double a, 
                                                             int d, int e, int unknowns) {
        int numUnknowns = Bitset.size(unknowns);
        
        if (numUnknowns > 2) {
            return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_TOO_MANY_UNKNOWNS_GEOMETRIC);
        }
        
        int allowedUnknowns = VarSets.VAR_N + VarSets.VAR_PV + VarSets.VAR_FV + VarSets.VAR_A;
        if (Bitset.isNotSubset(unknowns, allowedUnknowns)) {
            return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_UNKNOWN_UNKNOWNS_GEOMETRIC);
        }
        
        double iq = Percentages.percentageToFraction(i);
        double gq = Percentages.percentageToFraction(g);
        double iq1 = 1 + iq;
        double gq1 = 1 + gq;
        double giq = gq - iq;
        
        if (Math.abs(i - g) < MrfflConfig.ZERO_EPSILON) {
            if (Bitset.isSubset(VarSets.VAR_N, unknowns)) {
                if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    final double finalA = a;
                    final double finalFv = fv;
                    final double finalPv = pv;
                    final double finalIq1 = iq1;
                    final int finalD = d;
                    final int finalE = e;
                    DoubleUnaryOperator sfN = nVal -> {
                        double fvCalc = Math.pow(finalIq1, nVal - finalD) * (nVal - finalE - finalD + 1) * finalA;
                        double pvCalc = Math.pow(finalIq1, -finalD) * (nVal - finalE - finalD + 1) * finalA;
                        return fvCalc / pvCalc - finalFv / finalPv;
                    };
                    
                    double[] x0Inits = {1.0};
                    double[] x1Inits = {1000.0};
                    SolverResult solverResult = Solver.multiBisection(x0Inits, x1Inits, sfN, 
                                                                       MrfflConfig.ZERO_EPSILON, MrfflConfig.ZERO_EPSILON, 
                                                                       1000, false);
                    if (solverResult.getStatus() != 0) {
                        return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_GEOMETRIC_N_FV_SOLVER_FAILED);
                    }
                    n = solverResult.getXc();
                    fv = Math.pow(iq1, n - d) * (n - e - d + 1) * a;
                } else {
                    n = fv / (Math.pow(iq1, n - d) * a) + e + d - 1;
                }
            }
            
            if (Bitset.isSubset(VarSets.VAR_A, unknowns)) {
                a = fv / (Math.pow(iq1, n - d) * (n - e - d + 1));
            }
            
            if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                fv = Math.pow(iq1, n - d) * (n - e - d + 1) * a;
            }
            
            if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                pv = Math.pow(iq1, -d) * (n - e - d + 1) * a;
            }
        } else {
            if (Bitset.isSubset(VarSets.VAR_A + VarSets.VAR_PV, unknowns)) {
                final double finalPv = pv;
                final double finalGq1 = gq1;
                final double finalIq1 = iq1;
                final double finalN = n;
                final int finalE = e;
                final int finalD = d;
                final double finalGiq = giq;
                DoubleUnaryOperator sfAPv = aVal -> {
                    double pvCalc = (finalGq1 * Math.pow(finalGq1 / finalIq1, finalN - finalE) - Math.pow(finalGq1 / finalIq1, finalD) * finalIq1) * Math.pow(finalGq1, -finalD) * aVal / finalGiq;
                    return pvCalc - finalPv;
                };
                
                double[] x0Inits = {-1000.0};
                double[] x1Inits = {1000.0};
                SolverResult solverResult = Solver.multiBisection(x0Inits, x1Inits, sfAPv, 
                                                                   MrfflConfig.ZERO_EPSILON, MrfflConfig.ZERO_EPSILON, 
                                                                   1000, false);
                if (solverResult.getStatus() != 0) {
                    return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_GEOMETRIC_A_SOLVER_FAILED);
                }
                a = solverResult.getXc();
                pv = (gq1 * Math.pow(gq1 / iq1, n - e) - Math.pow(gq1 / iq1, d) * iq1) * Math.pow(gq1, -d) * a / giq;
            } else if (Bitset.isSubset(VarSets.VAR_A + VarSets.VAR_FV, unknowns)) {
                a = fv * giq / (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d));
                fv = 1 / giq * (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d)) * a;
            } else if (Bitset.isSubset(VarSets.VAR_A + VarSets.VAR_N, unknowns)) {
                final double finalA = a;
                final double finalFv = fv;
                final double finalPv = pv;
                final double finalGq1 = gq1;
                final double finalIq1 = iq1;
                final int finalD = d;
                final int finalE = e;
                final double finalGiq = giq;
                DoubleUnaryOperator sfAN = nVal -> {
                    double fvCalc = 1 / finalGiq * (Math.pow(finalGq1, -finalD + nVal - finalE + 1) * Math.pow(finalIq1, finalE) - Math.pow(finalIq1, 1 + nVal - finalD)) * finalA;
                    double pvCalc = (finalGq1 * Math.pow(finalGq1 / finalIq1, nVal - finalE) - Math.pow(finalGq1 / finalIq1, finalD) * finalIq1) * Math.pow(finalGq1, -finalD) * finalA / finalGiq;
                    return fvCalc / pvCalc - finalFv / finalPv;
                };
                
                double[] x0Inits = {1.0};
                double[] x1Inits = {1000.0};
                SolverResult solverResult = Solver.multiBisection(x0Inits, x1Inits, sfAN, 
                                                                   MrfflConfig.ZERO_EPSILON, MrfflConfig.ZERO_EPSILON, 
                                                                   1000, false);
                if (solverResult.getStatus() != 0) {
                    return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_GEOMETRIC_N_SOLVER_FAILED);
                }
                n = solverResult.getXc();
                a = fv * giq / (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d));
            } else if (Bitset.isSubset(VarSets.VAR_N + VarSets.VAR_FV, unknowns)) {
                final double finalA = a;
                final double finalFv = fv;
                final double finalPv = pv;
                final double finalGq1 = gq1;
                final double finalIq1 = iq1;
                final int finalD = d;
                final int finalE = e;
                final double finalGiq = giq;
                DoubleUnaryOperator sfNFv = nVal -> {
                    double fvCalc = 1 / finalGiq * (Math.pow(finalGq1, -finalD + nVal - finalE + 1) * Math.pow(finalIq1, finalE) - Math.pow(finalIq1, 1 + nVal - finalD)) * finalA;
                    double pvCalc = (finalGq1 * Math.pow(finalGq1 / finalIq1, nVal - finalE) - Math.pow(finalGq1 / finalIq1, finalD) * finalIq1) * Math.pow(finalGq1, -finalD) * finalA / finalGiq;
                    return fvCalc / pvCalc - finalFv / finalPv;
                };
                
                double[] x0Inits = {1.0};
                double[] x1Inits = {1000.0};
                SolverResult solverResult = Solver.multiBisection(x0Inits, x1Inits, sfNFv, 
                                                                   MrfflConfig.ZERO_EPSILON, MrfflConfig.ZERO_EPSILON, 
                                                                   1000, false);
                if (solverResult.getStatus() != 0) {
                    return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_GEOMETRIC_N_FV_SOLVER_FAILED);
                }
                n = solverResult.getXc();
                fv = 1 / giq * (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d)) * a;
            } else if (Bitset.isSubset(VarSets.VAR_PV + VarSets.VAR_FV, unknowns)) {
                final double finalFv = fv;
                final double finalIq1 = iq1;
                final double finalN = n;
                DoubleUnaryOperator sfPvFv = pvVal -> {
                    double fvCalc = pvVal * Math.pow(finalIq1, finalN);
                    return fvCalc - finalFv;
                };
                
                double[] x0Inits = {-1000.0};
                double[] x1Inits = {1000.0};
                SolverResult solverResult = Solver.multiBisection(x0Inits, x1Inits, sfPvFv, 
                                                                   MrfflConfig.ZERO_EPSILON, MrfflConfig.ZERO_EPSILON, 
                                                                   1000, false);
                if (solverResult.getStatus() != 0) {
                    return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_GEOMETRIC_PV_FV_SOLVER_FAILED);
                }
                pv = solverResult.getXc();
                fv = pv * Math.pow(iq1, n);
            } else {
                if (Bitset.isSubset(VarSets.VAR_FV, unknowns)) {
                    fv = 1 / giq * (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d)) * a;
                }
                if (Bitset.isSubset(VarSets.VAR_PV, unknowns)) {
                    pv = (gq1 * Math.pow(gq1 / iq1, n - e) - Math.pow(gq1 / iq1, d) * iq1) * Math.pow(gq1, -d) * a / giq;
                }
            }
        }
        
        return tvmDelayedGeometricAnnuityCheck(n, i, g, pv, fv, a, d, e);
    }
    
    /**
     * Check TVM parameters for a geometric annuity certain.
     * 
     * @param n Number of compounding periods
     * @param i Discount rate as a percentage
     * @param g Payment growth rate as a percentage
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero
     * @param e Early end counted from time end
     * @return TvmResult with status indicating consistency
     */
    public static TvmResult tvmDelayedGeometricAnnuityCheck(double n, double i, double g, double pv, double fv, double a, 
                                                             int d, int e) {
        double iq = Percentages.percentageToFraction(i);
        double gq = Percentages.percentageToFraction(g);
        double iq1 = 1 + iq;
        double gq1 = 1 + gq;
        double giq = gq - iq;
        
        if (Math.abs(i - g) < MrfflConfig.ZERO_EPSILON) {
            double expectedFv = Math.pow(iq1, n - d) * (n - e - d + 1) * a;
            double expectedPv = Math.pow(iq1, -d) * (n - e - d + 1) * a;
            
            if (Math.abs(expectedFv - fv) > CONSISTENT_EPSILON) {
                return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_INCONSISTENT_GEOMETRIC);
            }
            if (Math.abs(expectedPv - pv) > CONSISTENT_EPSILON) {
                return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_INCONSISTENT_GEOMETRIC);
            }
        } else {
            double expectedFv = 1 / giq * (Math.pow(gq1, -d + n - e + 1) * Math.pow(iq1, e) - Math.pow(iq1, 1 + n - d)) * a;
            double expectedPv = (gq1 * Math.pow(gq1 / iq1, n - e) - Math.pow(gq1 / iq1, d) * iq1) * Math.pow(gq1, -d) * a / giq;
            
            if (Math.abs(expectedFv - fv) > CONSISTENT_EPSILON) {
                return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_INCONSISTENT_GEOMETRIC);
            }
            if (Math.abs(expectedPv - pv) > CONSISTENT_EPSILON) {
                return new TvmResult(n, i, pv, fv, a, g, 0, ERROR_INCONSISTENT_GEOMETRIC);
            }
        }
        
        return new TvmResult(n, i, pv, fv, a, g, 0, 0);
    }
    
    /**
     * Solve for TVM parameters for an arithmetic annuity certain.
     * Can solve one or two variables except certain combinations.
     * Cannot solve: var_n+var_i, var_n+var_q, var_n+var_pv, var_n+var_fv, var_i+var_pv, var_i+var_fv, var_q+var_a
     * 
     * @param n Number of compounding periods
     * @param i Discount rate as a percentage
     * @param q Payment growth rate (added at each payment)
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero (d=0 is beginning of period 1, d=j is end of period j)
     * @param e Early end counted from time end (e=0 means last payment at end of period n)
     * @param unknowns Variables to solve for
     * @return TvmResult with status indicating success (0) or error code
     */
    public static TvmResult tvmDelayedArithmeticAnnuitySolve(double n, double i, double q, double pv, double fv, double a, 
                                                               int d, int e, int unknowns) {
        int curUnk = unknowns;
        int lstUnk = curUnk;
        double iq = Percentages.percentageToFraction(i);
        double iq1 = 1 + iq;
        double epd = e + d;
        double n1 = n + 1;
        
        while (true) {
            if (Bitset.isSubset(VarSets.VAR_I, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_FV + VarSets.VAR_PV + VarSets.VAR_N)) {
                iq = Math.pow(fv / pv, 1.0 / n) - 1;
                i = Percentages.fractionToPercentage(iq);
                iq1 = 1 + iq;
                curUnk = Bitset.minus(curUnk, VarSets.VAR_I);
            }
            
            if (Bitset.isSubset(VarSets.VAR_N, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_FV + VarSets.VAR_PV + VarSets.VAR_I)) {
                n = Math.log(fv / pv) / Math.log(iq1);
                n1 = n + 1;
                curUnk = Bitset.minus(curUnk, VarSets.VAR_N);
            }
            
            if (Bitset.isSubset(VarSets.VAR_PV, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_FV + VarSets.VAR_I + VarSets.VAR_N)) {
                pv = fv / Math.pow(iq1, n);
                curUnk = Bitset.minus(curUnk, VarSets.VAR_PV);
            }
            
            if (Bitset.isSubset(VarSets.VAR_FV, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_PV + VarSets.VAR_I + VarSets.VAR_N)) {
                fv = pv * Math.pow(iq1, n);
                curUnk = Bitset.minus(curUnk, VarSets.VAR_FV);
            }
            
            if (Bitset.isSubset(VarSets.VAR_A, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_I + VarSets.VAR_N + VarSets.VAR_Q + VarSets.VAR_FV)) {
                a = Math.pow(iq1, d) * (Math.pow(iq1, n1 - d) * q + q * (-1 + (epd - n1) * iq) * Math.pow(iq1, e) - fv * iq * iq) / iq / (Math.pow(iq1, epd) - Math.pow(iq1, n1));
                curUnk = Bitset.minus(curUnk, VarSets.VAR_A);
            }
            
            if (Bitset.isSubset(VarSets.VAR_Q, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_I + VarSets.VAR_A + VarSets.VAR_PV + VarSets.VAR_N)) {
                q = iq * (a * Math.pow(iq1, epd) + pv * iq * Math.pow(iq1, d + n) - a * Math.pow(iq1, n) * iq1) / ((-1 + (epd - n1) * iq) * Math.pow(iq1, epd) + Math.pow(iq1, n1));
                curUnk = Bitset.minus(curUnk, VarSets.VAR_Q);
            }
            
            if (Bitset.isSubset(VarSets.VAR_PV, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_N + VarSets.VAR_Q + VarSets.VAR_A + VarSets.VAR_I)) {
                pv = ((((epd - n1) * q - a) * iq - q) * Math.pow(1.0 / iq1, n - e) + (a * iq + q) * iq1 * Math.pow(1.0 / iq1, d)) / (iq * iq);
                curUnk = Bitset.minus(curUnk, VarSets.VAR_PV);
            }
            
            if (Bitset.isSubset(VarSets.VAR_FV, curUnk) && Bitset.hasNoIntersection(curUnk, VarSets.VAR_N + VarSets.VAR_Q + VarSets.VAR_A + VarSets.VAR_I)) {
                fv = (Math.pow(iq1, n1 - d) * (a * iq + q) - Math.pow(iq1, e) * (((n1 - e - d) * q + a) * iq + q)) / (iq * iq);
                curUnk = Bitset.minus(curUnk, VarSets.VAR_FV);
            }
            
            if (curUnk == lstUnk) {
                break;
            }
            lstUnk = curUnk;
        }
        
        if (Bitset.size(curUnk) > 0) {
            return new TvmResult(n, i, pv, fv, a, 0, q, ERROR_ARITHMETIC_UNABLE_TO_SOLVE);
        }
        
        return tvmDelayedArithmeticAnnuityCheck(n, i, q, pv, fv, a, d, e);
    }
    
    /**
     * Check TVM parameters for an arithmetic annuity certain.
     * 
     * @param n Number of compounding periods
     * @param i Discount rate as a percentage
     * @param q Payment growth rate (added at each payment)
     * @param pv Present Value
     * @param fv Future Value
     * @param a First payment (Annuity)
     * @param d Delay from time zero
     * @param e Early end counted from time end
     * @return TvmResult with status indicating consistency
     */
    public static TvmResult tvmDelayedArithmeticAnnuityCheck(double n, double i, double q, double pv, double fv, double a, 
                                                               int d, int e) {
        double iq = Percentages.percentageToFraction(i);
        double iq1 = 1 + iq;
        double n1 = n + 1;
        double epd = e + d;
        
        double expectedFv = (Math.pow(iq1, n1 - d) * (a * iq + q) - Math.pow(iq1, e) * (((n1 - e - d) * q + a) * iq + q)) / (iq * iq);
        double expectedPv = ((((d - 1 - n + e) * q - a) * iq - q) * Math.pow(1.0 / iq1, n - e) + (a * iq + q) * iq1 * Math.pow(1.0 / iq1, d)) / (iq * iq);
        
        if (Math.abs(expectedFv - fv) > CONSISTENT_EPSILON) {
            return new TvmResult(n, i, pv, fv, a, 0, q, ERROR_INCONSISTENT_ARITHMETIC);
        }
        if (Math.abs(expectedPv - pv) > CONSISTENT_EPSILON) {
            return new TvmResult(n, i, pv, fv, a, 0, q, ERROR_INCONSISTENT_ARITHMETIC);
        }
        
        return new TvmResult(n, i, pv, fv, a, 0, q, 0);
    }
}
