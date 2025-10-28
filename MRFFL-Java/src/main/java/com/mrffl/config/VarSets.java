package com.mrffl.config;

/**
 * Variable set constants for TVM (Time Value of Money) calculations.
 * 
 * These constants are used as bitset flags to identify which variables
 * are known or unknown in TVM solver routines. Each constant represents
 * a single bit position that can be combined using bitwise OR operations.
 * 
 * Corresponds to MRFFL/src/mrffl_var_sets.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class VarSets {
    
    /**
     * No variables specified.
     */
    public static final int VAR_NONE = 0;
    
    /**
     * First annuity payment (bit 0).
     * Used to identify the first payment amount in annuity calculations.
     */
    public static final int VAR_A = 1;
    
    /**
     * Principal (bit 1).
     * The initial amount or principal value in financial calculations.
     */
    public static final int VAR_P = 2;
    
    /**
     * Interest rate (bit 2).
     * The periodic interest rate used in time value calculations.
     */
    public static final int VAR_I = 4;
    
    /**
     * Growth rate for geometric annuity (bit 3).
     * Used when payments grow at a geometric rate (e.g., inflation adjustments).
     */
    public static final int VAR_G = 8;
    
    /**
     * Number of periods (bit 4).
     * The total number of compounding or payment periods.
     */
    public static final int VAR_N = 16;
    
    /**
     * Present value (bit 5).
     * The current value of future cash flows.
     */
    public static final int VAR_PV = 32;
    
    /**
     * Future value (bit 6).
     * The value of current cash flows at a future date.
     */
    public static final int VAR_FV = 64;
    
    /**
     * Delayed start (bit 7).
     * Number of periods before payments begin in a delayed annuity.
     */
    public static final int VAR_D = 128;
    
    /**
     * Early end (bit 8).
     * Number of periods before scheduled end when payments stop.
     */
    public static final int VAR_E = 256;
    
    /**
     * Arithmetic growth rate (bit 9).
     * Used when payments grow by a fixed amount each period.
     */
    public static final int VAR_Q = 512;
    
    /**
     * Payment amount (bit 10).
     * Used in TVM12 calculator-style computations.
     */
    public static final int VAR_PMT = 1024;
    
    private VarSets() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
