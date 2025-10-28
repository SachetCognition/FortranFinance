package com.mrffl.cashflow;

import com.mrffl.config.MrfflConfig;
import com.mrffl.config.PrtSets;
import com.mrffl.solver.Solver;
import com.mrffl.solver.SolverResult;
import com.mrffl.util.Bitset;
import com.mrffl.util.Percentages;

import java.util.function.DoubleUnaryOperator;

public final class Cashflows {
    
    public static final int SUCCESS = 0;
    
    public static final int ERROR_CF_ARITH_N_LT_1 = 2001;
    public static final int ERROR_CF_ARITH_D_LT_0 = 2002;
    public static final int ERROR_CF_ARITH_D_GT_N = 2003;
    public static final int ERROR_CF_ARITH_E_LT_0 = 2004;
    public static final int ERROR_CF_ARITH_E_GT_N = 2005;
    public static final int ERROR_CF_ARITH_D_PLUS_E_GT_N = 2006;
    
    public static final int ERROR_CF_GEOM_N_LT_1 = 2033;
    public static final int ERROR_CF_GEOM_D_LT_0 = 2034;
    public static final int ERROR_CF_GEOM_D_GT_N = 2035;
    public static final int ERROR_CF_GEOM_E_LT_0 = 2036;
    public static final int ERROR_CF_GEOM_E_GT_N = 2037;
    public static final int ERROR_CF_GEOM_D_PLUS_E_GT_N = 2038;
    
    public static final int ERROR_CF_LEVEL_N_LT_1 = 2065;
    public static final int ERROR_CF_LEVEL_D_LT_0 = 2066;
    public static final int ERROR_CF_LEVEL_D_GT_N = 2067;
    public static final int ERROR_CF_LEVEL_E_LT_0 = 2068;
    public static final int ERROR_CF_LEVEL_E_GT_N = 2069;
    public static final int ERROR_CF_LEVEL_D_PLUS_E_GT_N = 2070;
    
    public static final int ERROR_CF_LUMP_N_LT_1 = 2097;
    public static final int ERROR_CF_LUMP_D_LT_0 = 2098;
    public static final int ERROR_CF_LUMP_D_GT_N = 2099;
    
    public static final int ERROR_CF_MATRIX_NO_FLOWS = 2130;
    public static final int ERROR_CF_MATRIX_I_NEAR_MINUS_100 = 2132;
    public static final int ERROR_CF_MATRIX_N_TOO_SMALL = 2133;
    public static final int ERROR_CF_MATRIX_PV_VEC_TOO_SHORT = 2134;
    public static final int ERROR_CF_MATRIX_FV_VEC_TOO_SHORT = 2135;
    
    public static final int ERROR_CF_VECTOR_IRR_FAILED = 4161;
    public static final int ERROR_CF_MATRIX_IRR_FAILED = 4193;
    
    public static double cashflowVectorTotalPv(double[] cfVec, double i) {
        double totalPv = 0.0;
        double iFrac = Percentages.percentageToFraction(i);
        for (int j = 0; j < cfVec.length; j++) {
            totalPv += cfVec[j] / Math.pow(1 + iFrac, j);
        }
        return totalPv;
    }
    
    public static double cashflowMatrixTotalPv(double[][] cfMat, double i) {
        double totalPv = 0.0;
        double iFrac = Percentages.percentageToFraction(i);
        int numBoundaries = cfMat.length;
        int numFlows = cfMat[0].length;
        
        for (int j = 0; j < numBoundaries; j++) {
            double cf = 0.0;
            for (int k = 0; k < numFlows; k++) {
                cf += cfMat[j][k];
            }
            totalPv += cf / Math.pow(1 + iFrac, j);
        }
        return totalPv;
    }
    
    public static CashflowIrrResult cashflowVectorIrr(double[] cfVec, double irrInitial) {
        double[] ivl0 = {0.0 + MrfflConfig.ZERO_EPSILON, -100.0 + MrfflConfig.ZERO_EPSILON, -99999.0};
        double[] ivl1 = {99999.0, 0.0 - MrfflConfig.ZERO_EPSILON, -100.0 - MrfflConfig.ZERO_EPSILON};
        
        DoubleUnaryOperator irrSolve = i -> cashflowVectorTotalPv(cfVec, i);
        
        SolverResult result = Solver.multiBisection(ivl0, ivl1, irrSolve, 1.0e-5, 1.0e-5, 1000, false);
        
        if (result.getStatus() != 0) {
            return new CashflowIrrResult(irrInitial, ERROR_CF_VECTOR_IRR_FAILED);
        }
        
        return new CashflowIrrResult(result.getXc(), SUCCESS);
    }
    
    public static CashflowIrrResult cashflowMatrixIrr(double[][] cfMat, double irrInitial) {
        double[] ivl0 = {0.0 + MrfflConfig.ZERO_EPSILON, -100.0 + MrfflConfig.ZERO_EPSILON, -99999.0};
        double[] ivl1 = {99999.0, 0.0 - MrfflConfig.ZERO_EPSILON, -100.0 - MrfflConfig.ZERO_EPSILON};
        
        DoubleUnaryOperator irrSolve = i -> cashflowMatrixTotalPv(cfMat, i);
        
        SolverResult result = Solver.multiBisection(ivl0, ivl1, irrSolve, 1.0e-5, 1.0e-5, 1000, false);
        
        if (result.getStatus() != 0) {
            return new CashflowIrrResult(irrInitial, ERROR_CF_MATRIX_IRR_FAILED);
        }
        
        return new CashflowIrrResult(result.getXc(), SUCCESS);
    }
    
    public static CashflowPvFvResult cashflowMatrixPvFv(double[][] cfMat, double i) {
        int numBoundaries = cfMat.length;
        int numFlows = cfMat[0].length;
        
        if (numFlows < 1) {
            return new CashflowPvFvResult(new double[0], new double[0], ERROR_CF_MATRIX_NO_FLOWS);
        }
        if (Math.abs(i + 100) < MrfflConfig.ZERO_EPSILON) {
            return new CashflowPvFvResult(new double[0], new double[0], ERROR_CF_MATRIX_I_NEAR_MINUS_100);
        }
        if (numBoundaries < 2) {
            return new CashflowPvFvResult(new double[0], new double[0], ERROR_CF_MATRIX_N_TOO_SMALL);
        }
        
        double[] cfAggr = new double[numBoundaries];
        for (int j = 0; j < numBoundaries; j++) {
            for (int k = 0; k < numFlows; k++) {
                cfAggr[j] += cfMat[j][k];
            }
        }
        
        double iFrac = Percentages.percentageToFraction(i);
        double[] dfactors = new double[numBoundaries];
        for (int j = 0; j < numBoundaries; j++) {
            dfactors[j] = Math.pow(1 + iFrac, j);
        }
        
        double[] pvVec = new double[numBoundaries];
        double[] fvVec = new double[numBoundaries];
        for (int j = 0; j < numBoundaries; j++) {
            pvVec[j] = cfAggr[j] / dfactors[j];
            fvVec[j] = cfAggr[j] * dfactors[numBoundaries - 1 - j];
        }
        
        return new CashflowPvFvResult(pvVec, fvVec, SUCCESS);
    }
    
    public static CashflowVectorResult makeCashflowVectorDelayedLump(int n, double a, int d) {
        if (n < 1) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LUMP_N_LT_1);
        }
        if (d < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LUMP_D_LT_0);
        }
        if (d > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LUMP_D_GT_N);
        }
        
        double[] cfVec = new double[n + 1];
        cfVec[d] = a;
        return new CashflowVectorResult(cfVec, SUCCESS);
    }
    
    public static CashflowVectorResult makeCashflowVectorDelayedLevelAnnuity(int n, double a, int d, int e) {
        if (n < 1) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_N_LT_1);
        }
        if (d < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_D_LT_0);
        }
        if (d > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_D_GT_N);
        }
        if (e < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_E_LT_0);
        }
        if (e > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_E_GT_N);
        }
        if ((d + e) > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_LEVEL_D_PLUS_E_GT_N);
        }
        
        double[] cfVec = new double[n + 1];
        for (int j = d; j <= n - e; j++) {
            cfVec[j] = a;
        }
        return new CashflowVectorResult(cfVec, SUCCESS);
    }
    
    public static CashflowVectorResult makeCashflowVectorDelayedGeometricAnnuity(int n, double g, double a, int d, int e) {
        if (n < 1) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_N_LT_1);
        }
        if (d < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_D_LT_0);
        }
        if (d > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_D_GT_N);
        }
        if (e < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_E_LT_0);
        }
        if (e > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_E_GT_N);
        }
        if ((d + e) > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_GEOM_D_PLUS_E_GT_N);
        }
        
        double[] cfVec = new double[n + 1];
        double gFrac = Percentages.percentageToFraction(g);
        for (int j = d, k = 0; j <= n - e; j++, k++) {
            cfVec[j] = a * Math.pow(1 + gFrac, k);
        }
        return new CashflowVectorResult(cfVec, SUCCESS);
    }
    
    public static CashflowVectorResult makeCashflowVectorDelayedArithmeticAnnuity(int n, double q, double a, int d, int e) {
        if (n < 1) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_N_LT_1);
        }
        if (d < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_D_LT_0);
        }
        if (d > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_D_GT_N);
        }
        if (e < 0) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_E_LT_0);
        }
        if (e > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_E_GT_N);
        }
        if ((d + e) > n) {
            return new CashflowVectorResult(new double[0], ERROR_CF_ARITH_D_PLUS_E_GT_N);
        }
        
        double[] cfVec = new double[n + 1];
        for (int j = d, k = 0; j <= n - e; j++, k++) {
            cfVec[j] = a + q * k;
        }
        return new CashflowVectorResult(cfVec, SUCCESS);
    }
    
    public static int addInterestToCashflowVector(double[] cfVec, double rate) {
        int nb = cfVec.length;
        if (nb > 1) {
            double rsum = cfVec[0];
            for (int j = 1; j < nb; j++) {
                cfVec[j] = cfVec[j] + rsum * Percentages.percentageToFraction(rate);
                rsum = rsum + cfVec[j];
            }
        }
        return SUCCESS;
    }
    
    public static int addMultiInterestToCashflowVector(double[] cfVec, double[] vrate) {
        int nb = cfVec.length;
        if (nb > 1) {
            if (vrate.length < (nb - 1)) {
                return 4065;
            }
            double rsum = cfVec[0];
            for (int j = 1; j < nb; j++) {
                cfVec[j] = cfVec[j] + rsum * Percentages.percentageToFraction(vrate[j - 1]);
                rsum = rsum + cfVec[j];
            }
        }
        return SUCCESS;
    }
    
    private Cashflows() {
        throw new AssertionError("Utility class");
    }
}
