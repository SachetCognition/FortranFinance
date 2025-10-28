package com.mrffl.tvm;

import com.mrffl.config.MrfflConfig;
import com.mrffl.config.VarSets;
import com.mrffl.solver.Solver;
import com.mrffl.solver.SolverResult;

import java.util.function.DoubleUnaryOperator;

public final class Tvm12 {
    
    public static final int PMT_AT_BEGINNING = 1;
    public static final int PMT_AT_END = 0;
    
    public static final int SUCCESS = 0;
    public static final int ERROR_N_ZERO = 3001;
    public static final int ERROR_N_NEGATIVE = 3002;
    public static final int ERROR_I_NEAR_ZERO = 3003;
    public static final int ERROR_I_NEAR_MINUS_ONE = 3004;
    public static final int ERROR_UNABLE_TO_SOLVE_I = 3005;
    public static final int ERROR_CANNOT_SOLVE_N_BEGIN = 3006;
    public static final int ERROR_UNABLE_TO_SOLVE_I_END = 3007;
    public static final int ERROR_CANNOT_SOLVE_N_END = 3008;
    public static final int ERROR_UNSUPPORTED_UNKNOWN = 3009;
    public static final int ERROR_UNSUPPORTED_PMT_TIME = 3010;
    
    public static Tvm12Result tvm12Solve(int n, double i, double pv, double pmt, 
                                          double fv, int pmtTime, int unknown) {
        if (unknown != VarSets.VAR_N) {
            if (n == 0) {
                return new Tvm12Result(n, i, pv, pmt, fv, ERROR_N_ZERO);
            }
            if (n < 0) {
                return new Tvm12Result(n, i, pv, pmt, fv, ERROR_N_NEGATIVE);
            }
        }
        
        if (unknown != VarSets.VAR_I) {
            if (Math.abs(i) < MrfflConfig.ZERO_EPSILON) {
                return new Tvm12Result(n, i, pv, pmt, fv, ERROR_I_NEAR_ZERO);
            } else if (Math.abs(1 + i) < MrfflConfig.ZERO_EPSILON) {
                return new Tvm12Result(n, i, pv, pmt, fv, ERROR_I_NEAR_MINUS_ONE);
            }
        }
        
        double ip1tn = Math.pow(1 + i, n);
        
        if (pmtTime == PMT_AT_BEGINNING) {
            if (unknown == VarSets.VAR_PMT) {
                pmt = i * (fv + ip1tn * pv) / (i + 1 - (1 + i) * ip1tn);
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_I) {
                final int finalN = n;
                final double finalPv = pv;
                final double finalPmt = pmt;
                final double finalFv = fv;
                final int finalPmtTime = pmtTime;
                
                DoubleUnaryOperator func = x -> {
                    return (((-finalPmtTime * finalPmt + finalFv) * x - finalPmt) * 
                            Math.pow(1 + x, -finalN) + 
                            (finalPmtTime * finalPmt + finalPv) * x + finalPmt) / x;
                };
                
                double[] ivl0 = {0.0 + MrfflConfig.ZERO_EPSILON, 
                                 -100.0 + MrfflConfig.ZERO_EPSILON, 
                                 -99999.0};
                double[] ivl1 = {99999.0, 
                                 0.0 - MrfflConfig.ZERO_EPSILON, 
                                 -100.0 - MrfflConfig.ZERO_EPSILON};
                
                SolverResult result = Solver.multiBisection(ivl0, ivl1, func,
                                                             1.0e-5, 1.0e-5, 1000, false);
                if (result.getStatus() != 0) {
                    return new Tvm12Result(n, i, pv, pmt, fv, ERROR_UNABLE_TO_SOLVE_I);
                }
                i = result.getXc();
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_N) {
                double tmp1 = ((-1 - i) * pmt - pv * i) / ((-1 - i) * pmt + fv * i);
                double tmp2 = 1 + i;
                if ((tmp1 < MrfflConfig.ZERO_EPSILON) || (tmp2 < MrfflConfig.ZERO_EPSILON)) {
                    return new Tvm12Result(n, i, pv, pmt, fv, ERROR_CANNOT_SOLVE_N_BEGIN);
                }
                n = (int) Math.round(-Math.log(tmp1) / Math.log(tmp2));
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_PV) {
                pv = ((pmt * (1 + i) - fv * i) * Math.pow(1 + i, -n) - pmt * (1 + i)) / i;
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_FV) {
                fv = (((-1 - i) * pmt - pv * i) * ip1tn + pmt * (1 + i)) / i;
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            }
        } else if (pmtTime == PMT_AT_END) {
            if (unknown == VarSets.VAR_PMT) {
                pmt = -i / (ip1tn - 1) * (fv + ip1tn * pv);
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_I) {
                final int finalN = n;
                final double finalPv = pv;
                final double finalPmt = pmt;
                final double finalFv = fv;
                final int finalPmtTime = pmtTime;
                
                DoubleUnaryOperator func = x -> {
                    return (((-finalPmtTime * finalPmt + finalFv) * x - finalPmt) * 
                            Math.pow(1 + x, -finalN) + 
                            (finalPmtTime * finalPmt + finalPv) * x + finalPmt) / x;
                };
                
                double[] ivl0 = {0.0 + MrfflConfig.ZERO_EPSILON, 
                                 -100.0 + MrfflConfig.ZERO_EPSILON, 
                                 -99999.0};
                double[] ivl1 = {99999.0, 
                                 0.0 - MrfflConfig.ZERO_EPSILON, 
                                 -100.0 - MrfflConfig.ZERO_EPSILON};
                
                SolverResult result = Solver.multiBisection(ivl0, ivl1, func,
                                                             1.0e-5, 1.0e-5, 1000, false);
                if (result.getStatus() != 0) {
                    return new Tvm12Result(n, i, pv, pmt, fv, ERROR_UNABLE_TO_SOLVE_I_END);
                }
                i = result.getXc();
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_N) {
                double tmp1 = (-pv * i - pmt) / (fv * i - pmt);
                double tmp2 = 1 + i;
                if ((tmp1 < MrfflConfig.ZERO_EPSILON) || (tmp2 < MrfflConfig.ZERO_EPSILON)) {
                    return new Tvm12Result(n, i, pv, pmt, fv, ERROR_CANNOT_SOLVE_N_END);
                }
                n = (int) Math.round(-Math.log(tmp1) / Math.log(tmp2));
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_PV) {
                pv = ((-fv * i + pmt) / ip1tn - pmt) / i;
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else if (unknown == VarSets.VAR_FV) {
                fv = ((-pv * i - pmt) * ip1tn + pmt) / i;
                return new Tvm12Result(n, i, pv, pmt, fv, SUCCESS);
            } else {
                return new Tvm12Result(n, i, pv, pmt, fv, ERROR_UNSUPPORTED_UNKNOWN);
            }
        } else {
            return new Tvm12Result(n, i, pv, pmt, fv, ERROR_UNSUPPORTED_PMT_TIME);
        }
        
        return new Tvm12Result(n, i, pv, pmt, fv, ERROR_UNSUPPORTED_UNKNOWN);
    }
    
    private Tvm12() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
