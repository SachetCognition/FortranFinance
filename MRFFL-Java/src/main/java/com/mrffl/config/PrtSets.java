package com.mrffl.config;

/**
 * Print set constants for controlling output formatting.
 * 
 * These constants are used as bitset flags to control which parts of
 * output are printed by various print routines throughout the library.
 * Each constant represents a single bit position that can be combined
 * using bitwise OR operations.
 * 
 * Corresponds to MRFFL/src/mrffl_prt_sets.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class PrtSets {
    
    /**
     * Print nothing.
     */
    public static final int PRT_NONE = 0;
    
    /**
     * Print parameters (bit 0).
     * Display input parameters and configuration values.
     */
    public static final int PRT_PARAM = 1;
    
    /**
     * Print table titles (bit 1).
     * Display column headers and table titles.
     */
    public static final int PRT_TITLE = 2;
    
    /**
     * Print table body (bit 2).
     * Display the main data rows of tables.
     */
    public static final int PRT_TABLE = 4;
    
    /**
     * Print totals (bit 3).
     * Display summary totals and aggregate values.
     */
    public static final int PRT_TOTAL = 8;
    
    /**
     * Print vertical whitespace (bit 4).
     * Add blank lines for readability.
     */
    public static final int PRT_SPACE = 16;
    
    /**
     * Print everything (all bits set).
     * Equivalent to PRT_PARAM | PRT_TITLE | PRT_TABLE | PRT_TOTAL | PRT_SPACE.
     */
    public static final int PRT_ALL = 31;
    
    private PrtSets() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
