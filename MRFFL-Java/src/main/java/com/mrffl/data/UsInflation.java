package com.mrffl.data;

import com.mrffl.config.MrfflConfig;
import com.mrffl.stats.Stats;
import com.mrffl.util.Percentages;

public final class UsInflation {
    
    public static final int FIRST_YEAR = 1914;
    public static final int LAST_YEAR = 2023;
    
    public static final double[] INF_DAT = {
        1.0,  1.0,  7.9, 17.4, 18.0, 14.6,
        15.6, -10.5, -6.1,  1.8,  0.0,  2.3,  1.1, -1.7, -1.7,  0.0,
        -2.3,  -9.0, -9.9, -5.1,  3.1,  2.2,  1.5,  3.6, -2.1, -1.4,
        0.7,   5.0, 10.9,  6.1,  1.7,  2.3,  8.3, 14.4,  8.1, -1.2,
        1.3,   7.9,  1.9,  0.8,  0.7, -0.4,  1.5,  3.3,  2.8,  0.7,
        1.7,   1.0,  1.0,  1.3,  1.3,  1.6,  2.9,  3.1,  4.2,  5.5,
        5.7,   4.4,  3.2,  6.2, 11.0,  9.1,  5.8,  6.5,  7.6, 11.3,
        13.5,  10.3,  6.2,  3.2,  4.3,  3.6,  1.9,  3.6,  4.1,  4.8,
        5.4,   4.2,  3.0,  3.0,  2.6,  2.8,  3.0,  2.3,  1.6,  2.2,
        3.4,   2.8,  1.6,  2.3,  2.7,  3.4,  3.2,  2.8,  3.8, -0.4,
        1.6,   3.2,  2.1,  1.5,  1.6,  0.1,  1.3,  2.1,  2.4,  1.8,
        1.2,   4.7,  8.0,  4.1
    };
    
    public static double infAggregate(int fromYear, int toYear) {
        if (fromYear < FIRST_YEAR || fromYear > LAST_YEAR) {
            throw new IllegalArgumentException("fromYear out of range: " + fromYear);
        }
        if (toYear < FIRST_YEAR || toYear > LAST_YEAR) {
            throw new IllegalArgumentException("toYear out of range: " + toYear);
        }
        
        int minYear = Math.min(fromYear, toYear);
        int maxYear = Math.max(fromYear, toYear);
        
        double result = 1.0;
        for (int year = minYear + 1; year <= maxYear; year++) {
            int idx = year - FIRST_YEAR;
            result = result + Percentages.percentageOf(result, INF_DAT[idx]);
        }
        
        if (fromYear > toYear) {
            return Percentages.percentageChange(result, 1.0);
        } else {
            return Percentages.percentageChange(1.0, result);
        }
    }
    
    public static double infAdj(int fromYear, int toYear, double value) {
        return Percentages.addPercentage(value, infAggregate(fromYear, toYear));
    }
    
    public static double infResample(int historyYears) {
        return Stats.resampleTail(INF_DAT, historyYears);
    }
    
    private UsInflation() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
