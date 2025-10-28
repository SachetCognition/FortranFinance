package com.mrffl.util;

/**
 * Bitset operations using integer bits to represent set membership.
 * 
 * These operations are used primarily for TVM solver variable tracking
 * (identifying unknown variables) and controlling output formatting
 * through print control flags.
 * 
 * Each bit in an integer represents membership of an element in the set.
 * Bitset operations allow efficient set manipulations using bitwise operators.
 * 
 * Corresponds to MRFFL/src/mrffl_bitset.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class Bitset {
    
    /**
     * Count the number of elements in a bitset (population count).
     * 
     * @param bitset the bitset to count
     * @return number of set bits (elements in the set)
     */
    public static int size(int bitset) {
        return Integer.bitCount(bitset);
    }
    
    /**
     * Compute set difference: elements in bitset1 but not in bitset2.
     * Equivalent to bitset1 - bitset2 in set notation.
     * 
     * @param bitset1 the first bitset
     * @param bitset2 the bitset to subtract
     * @return bitset representing bitset1 \ bitset2
     */
    public static int minus(int bitset1, int bitset2) {
        return bitset1 & ~bitset2;
    }
    
    /**
     * Test if bitset1 is a subset of bitset2.
     * Returns true if all elements in bitset1 are also in bitset2.
     * 
     * @param bitset1 the potential subset
     * @param bitset2 the potential superset
     * @return true if bitset1 ⊆ bitset2
     */
    public static boolean isSubset(int bitset1, int bitset2) {
        return (bitset1 & bitset2) == bitset1;
    }
    
    /**
     * Test if bitset1 is NOT a subset of bitset2.
     * Returns true if bitset1 contains at least one element not in bitset2.
     * 
     * @param bitset1 the potential subset
     * @param bitset2 the potential superset
     * @return true if bitset1 ⊄ bitset2
     */
    public static boolean isNotSubset(int bitset1, int bitset2) {
        return (bitset1 & bitset2) != bitset1;
    }
    
    /**
     * Test if two bitsets have a non-empty intersection.
     * Returns true if bitset1 and bitset2 share at least one element.
     * 
     * @param bitset1 the first bitset
     * @param bitset2 the second bitset
     * @return true if bitset1 ∩ bitset2 ≠ ∅
     */
    public static boolean hasIntersection(int bitset1, int bitset2) {
        return (bitset1 & bitset2) != 0;
    }
    
    /**
     * Test if two bitsets have an empty intersection.
     * Returns true if bitset1 and bitset2 share no elements.
     * 
     * @param bitset1 the first bitset
     * @param bitset2 the second bitset
     * @return true if bitset1 ∩ bitset2 = ∅
     */
    public static boolean hasNoIntersection(int bitset1, int bitset2) {
        return (bitset1 & bitset2) == 0;
    }
    
    private Bitset() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
