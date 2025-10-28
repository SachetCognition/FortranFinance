package com.mrffl.util;

import com.mrffl.config.VarSets;
import com.mrffl.config.PrtSets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bitset operations.
 */
public class BitsetTest {
    
    @Test
    public void testSize() {
        assertEquals(0, Bitset.size(0));
        assertEquals(1, Bitset.size(1));
        assertEquals(1, Bitset.size(2));
        assertEquals(2, Bitset.size(3));
        assertEquals(3, Bitset.size(7));
        assertEquals(4, Bitset.size(15));
        assertEquals(5, Bitset.size(31));
    }
    
    @Test
    public void testSizeWithVarSets() {
        assertEquals(1, Bitset.size(VarSets.VAR_A));
        assertEquals(1, Bitset.size(VarSets.VAR_P));
        assertEquals(2, Bitset.size(VarSets.VAR_A | VarSets.VAR_P));
        assertEquals(3, Bitset.size(VarSets.VAR_A | VarSets.VAR_P | VarSets.VAR_I));
    }
    
    @Test
    public void testMinus() {
        assertEquals(0, Bitset.minus(0, 0));
        assertEquals(1, Bitset.minus(1, 0));
        assertEquals(0, Bitset.minus(1, 1));
        assertEquals(1, Bitset.minus(3, 2));
        assertEquals(2, Bitset.minus(3, 1));
        assertEquals(4, Bitset.minus(7, 3));
    }
    
    @Test
    public void testMinusWithVarSets() {
        int both = VarSets.VAR_PV | VarSets.VAR_FV;
        int justPv = Bitset.minus(both, VarSets.VAR_FV);
        assertEquals(VarSets.VAR_PV, justPv);
        
        int justFv = Bitset.minus(both, VarSets.VAR_PV);
        assertEquals(VarSets.VAR_FV, justFv);
    }
    
    @Test
    public void testIsSubset() {
        assertTrue(Bitset.isSubset(0, 0));
        assertTrue(Bitset.isSubset(0, 1));
        assertTrue(Bitset.isSubset(1, 1));
        assertTrue(Bitset.isSubset(1, 3));
        assertTrue(Bitset.isSubset(2, 3));
        assertTrue(Bitset.isSubset(3, 3));
        assertFalse(Bitset.isSubset(3, 1));
        assertFalse(Bitset.isSubset(4, 3));
    }
    
    @Test
    public void testIsSubsetWithPrtSets() {
        assertTrue(Bitset.isSubset(PrtSets.PRT_PARAM, PrtSets.PRT_ALL));
        assertTrue(Bitset.isSubset(PrtSets.PRT_TITLE, PrtSets.PRT_ALL));
        assertTrue(Bitset.isSubset(
            PrtSets.PRT_PARAM | PrtSets.PRT_TITLE, 
            PrtSets.PRT_ALL
        ));
        assertFalse(Bitset.isSubset(PrtSets.PRT_ALL, PrtSets.PRT_PARAM));
    }
    
    @Test
    public void testIsNotSubset() {
        assertFalse(Bitset.isNotSubset(0, 0));
        assertFalse(Bitset.isNotSubset(0, 1));
        assertFalse(Bitset.isNotSubset(1, 1));
        assertFalse(Bitset.isNotSubset(1, 3));
        assertTrue(Bitset.isNotSubset(3, 1));
        assertTrue(Bitset.isNotSubset(4, 3));
    }
    
    @Test
    public void testHasIntersection() {
        assertFalse(Bitset.hasIntersection(0, 0));
        assertFalse(Bitset.hasIntersection(0, 1));
        assertFalse(Bitset.hasIntersection(1, 0));
        assertTrue(Bitset.hasIntersection(1, 1));
        assertTrue(Bitset.hasIntersection(1, 3));
        assertTrue(Bitset.hasIntersection(2, 3));
        assertFalse(Bitset.hasIntersection(1, 2));
        assertFalse(Bitset.hasIntersection(4, 3));
    }
    
    @Test
    public void testHasIntersectionWithVarSets() {
        assertTrue(Bitset.hasIntersection(VarSets.VAR_PV, VarSets.VAR_PV));
        assertTrue(Bitset.hasIntersection(
            VarSets.VAR_PV | VarSets.VAR_FV,
            VarSets.VAR_PV | VarSets.VAR_I
        ));
        assertFalse(Bitset.hasIntersection(VarSets.VAR_PV, VarSets.VAR_FV));
    }
    
    @Test
    public void testHasNoIntersection() {
        assertTrue(Bitset.hasNoIntersection(0, 0));
        assertTrue(Bitset.hasNoIntersection(0, 1));
        assertTrue(Bitset.hasNoIntersection(1, 0));
        assertFalse(Bitset.hasNoIntersection(1, 1));
        assertFalse(Bitset.hasNoIntersection(1, 3));
        assertTrue(Bitset.hasNoIntersection(1, 2));
        assertTrue(Bitset.hasNoIntersection(4, 3));
    }
    
    @Test
    public void testHasNoIntersectionWithPrtSets() {
        assertTrue(Bitset.hasNoIntersection(PrtSets.PRT_PARAM, PrtSets.PRT_TITLE));
        assertTrue(Bitset.hasNoIntersection(PrtSets.PRT_PARAM, PrtSets.PRT_TABLE));
        assertFalse(Bitset.hasNoIntersection(PrtSets.PRT_ALL, PrtSets.PRT_PARAM));
    }
    
    @Test
    public void testComplexBitsetOperations() {
        int set1 = VarSets.VAR_A | VarSets.VAR_P | VarSets.VAR_I;
        int set2 = VarSets.VAR_P | VarSets.VAR_N | VarSets.VAR_PV;
        
        assertEquals(3, Bitset.size(set1));
        assertEquals(3, Bitset.size(set2));
        
        assertTrue(Bitset.hasIntersection(set1, set2));
        
        int diff1 = Bitset.minus(set1, set2);
        assertEquals(VarSets.VAR_A | VarSets.VAR_I, diff1);
        
        int diff2 = Bitset.minus(set2, set1);
        assertEquals(VarSets.VAR_N | VarSets.VAR_PV, diff2);
    }
}
