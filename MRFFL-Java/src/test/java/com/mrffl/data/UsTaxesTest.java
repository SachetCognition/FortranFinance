package com.mrffl.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsTaxesTest {
    
    private static final double TOLERANCE = 1.0e-6;
    
    @Test
    public void testMaxBracketNegative() {
        assertEquals(1, UsTaxes.maxBracket(-1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketZero() {
        assertEquals(1, UsTaxes.maxBracket(0.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketOne() {
        assertEquals(1, UsTaxes.maxBracket(1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketFirstBreakMinus1() {
        assertEquals(1, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0] - 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketFirstBreakExact() {
        assertEquals(1, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0], UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketFirstBreakPlus1() {
        assertEquals(2, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0] + 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketSecondBreakMinus1() {
        assertEquals(2, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[1] - 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketSecondBreakExact() {
        assertEquals(2, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[1], UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketSecondBreakPlus1() {
        assertEquals(3, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[1] + 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketLastBreakMinus1() {
        int lastIdx = UsTaxes.TAX_BRACKET_BREAKS_SINGLE.length - 1;
        assertEquals(lastIdx, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[lastIdx] - 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketLastBreakExact() {
        int lastIdx = UsTaxes.TAX_BRACKET_BREAKS_SINGLE.length - 1;
        assertEquals(lastIdx + 1, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[lastIdx], UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testMaxBracketLastBreakPlus1() {
        int lastIdx = UsTaxes.TAX_BRACKET_BREAKS_SINGLE.length - 1;
        assertEquals(lastIdx + 1, UsTaxes.maxBracket(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[lastIdx] + 1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE));
    }
    
    @Test
    public void testEffectiveTaxRateNegative() {
        assertEquals(0.0, UsTaxes.effectiveTaxRate(-1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testEffectiveTaxRateZero() {
        assertEquals(0.0, UsTaxes.effectiveTaxRate(0.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testEffectiveTaxRateOne() {
        assertEquals(10.0, UsTaxes.effectiveTaxRate(1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testEffectiveTaxRateFirstBracket() {
        assertEquals(10.0, UsTaxes.effectiveTaxRate(UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0], UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testTaxNegativeIncome() {
        assertEquals(0.0, UsTaxes.tax(-1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testTaxZeroIncome() {
        assertEquals(0.0, UsTaxes.tax(0.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testTaxLowIncome() {
        double income = 1000.0;
        double expectedTax = 100.0;
        assertEquals(expectedTax, UsTaxes.tax(income, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testTaxFirstBracketBoundary() {
        double income = UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0];
        double expectedTax = income * 0.10;
        assertEquals(expectedTax, UsTaxes.tax(income, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testTaxSecondBracket() {
        double income = 50000.0;
        double tax1 = UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0] * 0.10;
        double tax2 = (UsTaxes.TAX_BRACKET_BREAKS_SINGLE[1] - UsTaxes.TAX_BRACKET_BREAKS_SINGLE[0]) * 0.12;
        double tax3 = (income - UsTaxes.TAX_BRACKET_BREAKS_SINGLE[1]) * 0.22;
        double expectedTax = tax1 + tax2 + tax3;
        assertEquals(expectedTax, UsTaxes.tax(income, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES), TOLERANCE);
    }
    
    @Test
    public void testProjectedTaxPastYear() {
        assertEquals(-1.0, UsTaxes.projectedTax(10000.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2023, 10.0), TOLERANCE);
    }
    
    @Test
    public void testProjectedTaxSeedYear() {
        double income = 10000.0;
        double expectedTax = UsTaxes.tax(income, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES);
        assertEquals(expectedTax, UsTaxes.projectedTax(income, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2024, 10.0), TOLERANCE);
    }
    
    @Test
    public void testProjectedTaxZeroIncomeFutureYear() {
        assertEquals(0.0, UsTaxes.projectedTax(0.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 12.0), TOLERANCE);
    }
    
    @Test
    public void testProjectedTaxLowIncomeFutureYear() {
        assertEquals(0.10, UsTaxes.projectedTax(1.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), TOLERANCE);
        assertEquals(10.0, UsTaxes.projectedTax(100.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), TOLERANCE);
        assertEquals(100.0, UsTaxes.projectedTax(1000.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), TOLERANCE);
        assertEquals(1000.0, UsTaxes.projectedTax(10000.0, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), TOLERANCE);
    }
    
    @Test
    public void testProjectedTaxInflationAdjustment() {
        double income1 = 12759.0;
        double expectedTax1 = income1 * 0.10;
        assertEquals(expectedTax1, UsTaxes.projectedTax(income1, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), 1.0);
        
        double income2 = 12760.0;
        double expectedTax2 = income2 * 0.10;
        assertEquals(expectedTax2, UsTaxes.projectedTax(income2, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), 1.0);
        
        double income3 = 12761.0;
        double expectedTax3 = 12760.0 * 0.10 + 0.12;
        assertEquals(expectedTax3, UsTaxes.projectedTax(income3, UsTaxes.TAX_BRACKET_BREAKS_SINGLE, UsTaxes.TAX_BRACKET_RATES, 2025, 10.0), 1.0);
    }
    
    @Test
    public void testConstantsLength() {
        assertEquals(7, UsTaxes.TAX_BRACKET_RATES.length);
        assertEquals(7, UsTaxes.TAX_BRACKET_BREAKS_SINGLE.length);
        assertEquals(7, UsTaxes.TAX_BRACKET_BREAKS_JOINT.length);
        assertEquals(7, UsTaxes.TAX_BRACKET_BREAKS_HEAD.length);
        assertEquals(7, UsTaxes.TAX_BRACKET_BREAKS_SEPARATELY.length);
    }
    
    @Test
    public void testStandardDeductions() {
        assertEquals(14600, UsTaxes.STD_TAX_DEDUCTION_SINGLE, TOLERANCE);
        assertEquals(29200, UsTaxes.STD_TAX_DEDUCTION_JOINT, TOLERANCE);
        assertEquals(14600, UsTaxes.STD_TAX_DEDUCTION_SEPARATELY, TOLERANCE);
        assertEquals(21900, UsTaxes.STD_TAX_DEDUCTION_HEAD, TOLERANCE);
    }
}
