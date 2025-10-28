package com.mrffl.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MrfflConfig.
 */
class MrfflConfigTest {
    
    @Test
    void testZeroEpsilon() {
        assertEquals(1.0e-8, MrfflConfig.ZERO_EPSILON, 1.0e-10);
    }
    
    @Test
    void testIsZero() {
        assertTrue(MrfflConfig.isZero(0.0));
        assertTrue(MrfflConfig.isZero(1.0e-9));
        assertTrue(MrfflConfig.isZero(-1.0e-9));
        assertFalse(MrfflConfig.isZero(1.0e-7));
        assertFalse(MrfflConfig.isZero(-1.0e-7));
    }
    
    @Test
    void testEquals() {
        assertTrue(MrfflConfig.equals(1.0, 1.0));
        assertTrue(MrfflConfig.equals(1.0, 1.0 + 1.0e-9));
        assertTrue(MrfflConfig.equals(1.0, 1.0 - 1.0e-9));
        assertFalse(MrfflConfig.equals(1.0, 1.0 + 1.0e-7));
        assertFalse(MrfflConfig.equals(1.0, 1.0 - 1.0e-7));
    }
    
    @Test
    void testFormatCashValue() {
        String formatted = MrfflConfig.formatCashValue(1234.5678);
        assertTrue(formatted.contains("1234.5678"));
        assertEquals(15, formatted.length());
    }
    
    @Test
    void testFormatCashTitle() {
        String formatted = MrfflConfig.formatCashTitle("Test");
        assertTrue(formatted.startsWith("Test"));
        assertEquals(15, formatted.length());
    }
}
