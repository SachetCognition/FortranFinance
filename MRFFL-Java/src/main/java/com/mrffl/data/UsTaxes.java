package com.mrffl.data;

import com.mrffl.config.MrfflConfig;
import com.mrffl.util.Percentages;
import com.mrffl.tvm.Tvm;

public final class UsTaxes {
    
    public static final int SEED_TAX_YEAR = 2024;
    
    public static final double STD_TAX_DEDUCTION_SINGLE = 14600;
    public static final double STD_TAX_DEDUCTION_JOINT = 29200;
    public static final double STD_TAX_DEDUCTION_SEPARATELY = 14600;
    public static final double STD_TAX_DEDUCTION_HEAD = 21900;
    
    public static final double[] TAX_BRACKET_RATES = {10, 12, 22, 24, 32, 35, 37};
    public static final double[] TAX_BRACKET_BREAKS_SINGLE = {11600, 47150, 100525, 191950, 243725, 609350, 609351};
    public static final double[] TAX_BRACKET_BREAKS_JOINT = {23200, 94300, 201050, 383900, 487450, 731200, 731201};
    public static final double[] TAX_BRACKET_BREAKS_HEAD = {16550, 63100, 100500, 191950, 243700, 609350, 609350};
    public static final double[] TAX_BRACKET_BREAKS_SEPARATELY = {11600, 47150, 100525, 191950, 243725, 365600, 365601};
    
    public static int maxBracket(double val, double[] brackets) {
        if (val <= brackets[0]) {
            return 1;
        } else if (val >= brackets[brackets.length - 1]) {
            return brackets.length;
        } else {
            int maxBracket = 0;
            for (int i = brackets.length - 2; i >= 0; i--) {
                if (val > brackets[i]) {
                    maxBracket = i + 2;
                    break;
                }
            }
            return maxBracket;
        }
    }
    
    public static double tax(double val, double[] breaks, double[] rates) {
        if (val < MrfflConfig.ZERO_EPSILON) {
            return 0.0;
        }
        
        double lastBreak = 0.0;
        double taxTotal = 0.0;
        
        for (int idx = 0; idx < rates.length - 1; idx++) {
            double curBreak = breaks[idx];
            double valInBracket = Math.min(val, curBreak) - lastBreak;
            lastBreak = curBreak;
            if (valInBracket > 0) {
                taxTotal += Percentages.percentageOf(valInBracket, rates[idx]);
            }
        }
        
        if (val >= breaks[breaks.length - 1]) {
            taxTotal += Percentages.percentageOf(1 + val - breaks[breaks.length - 1], rates[rates.length - 1]);
        }
        
        return taxTotal;
    }
    
    public static double effectiveTaxRate(double val, double[] breaks, double[] rates) {
        if (val < MrfflConfig.ZERO_EPSILON) {
            return 0.0;
        } else {
            return Percentages.percentageOfTotal(val, tax(val, breaks, rates));
        }
    }
    
    public static double projectedTax(double val, double[] breaks, double[] rates, int year, double inflation) {
        if (year < SEED_TAX_YEAR) {
            return -1.0;
        } else if (year == SEED_TAX_YEAR) {
            return tax(val, breaks, rates);
        } else {
            double[] adjustedBreaks = new double[breaks.length];
            for (int i = 0; i < breaks.length; i++) {
                adjustedBreaks[i] = Tvm.fvFromPvNI(breaks[i], year - SEED_TAX_YEAR, inflation);
            }
            return tax(val, adjustedBreaks, rates);
        }
    }
    
    private UsTaxes() {
        throw new AssertionError("Utility class");
    }
}
