package com.mrffl.data;

import com.mrffl.stats.Stats;

public final class UsMarkets {
    
    public static final double[] SNP_DAT = {
        3.94,  14.30,  18.99, -14.66, -26.47,  37.20,  23.84,  -7.18,   6.57,  18.44,
        32.42, -4.91,  21.41,  22.51,  -7.18,  6.51,  18.52,  31.74,  -4.70,  20.89,
        22.34, -9.73,  -11.85, -22.10,  28.36,  10.70,   4.83,  15.61,   5.48, -36.55,
        23.45,  12.78,  0.00,  13.41,  29.60,   11.39, -0.73,   9.54,  19.42,  31.01,
        -4.70,  20.89,  28.36,  10.34,   4.78,  15.63,   5.49, -36.55,  23.45,  12.78,
        0.00,  13.41,  29.60,  11.39
    };
    
    public static final double[] RUT_DAT = {
        24.89,  -8.84,  21.95, 18.39,  -1.57, -3.83,  22.36,  -4.18,  18.33,  37.96,
        -19.99, 25.31,  -1.49, 4.55,  46.49,  38.82,  -4.41, -33.79,  27.17,  26.85,
        16.35,  38.82, -4.18, 16.93,   1.57,  -4.41, 21.31,  -33.79, 27.17,  26.85,
        16.35,  38.82,  4.89,  14.65, -4.41,   8.83
    };
    
    public static final double[] NAS_DAT = {
        -35.11,  19.85,  31.08,  7.31,  29.75,  31.54,  -3.19, 22.95,  28.13, -11.88,
        18.00,  15.41,  15.58, 31.07,  -2.13,  40.50, 21.64, -39.29,  43.89,  16.91,
        -1.80,  15.91,  16.40,   8.59,  13.40,   5.73, -21.05,  43.64,  17.92, 15.46,
        29.64,   9.75,  -0.82,  18.33,  27.96,  43.23,  -3.88, 21.39,  31.96, -33.10,
        43.23, -33.47,  50.01,  39.63, 15.89, 43.64,   9.75,   5.73, -21.39,  43.89
    };
    
    public static final double[] DOW_DAT = {
        4.82,  6.11,  14.58,  16.58,  -27.57,  38.32,  17.86, -17.27,  -3.15,  14.76,
        25.77, -9.23,  20.23,  17.93,  2.26,   14.93,  22.58,   3.15,  16.10,  26.01,
        -6.17, 16.29,  22.64, -16.74, -3.74,  -8.99,  26.50,  3.79,   4.17,  13.81,
        5.53, -33.84,  18.82,  11.02,   7.26, 10.02,  26.50,   7.52,   2.04,  13.42,
        7.09, -37.85,  22.68,  11.80, 5.53, 16.29,  11.02,   7.26,  10.02,  26.50,
        7.09,  5.49,  13.81,  1.61
    };
    
    public static final double[] DGS10_DAT = {
        3.95,  4.00,  4.21,  4.26,  4.40,  4.66,  5.13,  5.57,  5.65,  5.84,
        6.59,  6.21,  6.67,  6.85,  7.08,  7.42,  7.42,  8.04,  8.41,  7.86,
        11.43,  13.91,  12.92,  11.67,  12.44, 10.62,  9.02,   8.39,  8.85,  8.55,
        8.04,  7.68,  7.01,  6.03,  5.87,  6.44,  6.64,  5.26,  5.02,  5.65,
        6.03,  4.27,  4.61,  5.63,  4.63,  4.29,  4.64,  4.80,  3.66,  3.21,
        2.79,  1.80,  2.14,  1.47,  2.33,  2.41,  2.14,  2.84,  0.93,  1.45,
        1.52,  2.95,  2.94,  4.25
    };
    
    public static double snpResample(int historyYears) {
        return Stats.resampleTail(SNP_DAT, historyYears);
    }
    
    public static double rutResample(int historyYears) {
        return Stats.resampleTail(RUT_DAT, historyYears);
    }
    
    public static double nasResample(int historyYears) {
        return Stats.resampleTail(NAS_DAT, historyYears);
    }
    
    public static double dowResample(int historyYears) {
        return Stats.resampleTail(DOW_DAT, historyYears);
    }
    
    public static double dgs10Resample(int historyYears) {
        return Stats.resampleTail(DGS10_DAT, historyYears);
    }
    
    private UsMarkets() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
