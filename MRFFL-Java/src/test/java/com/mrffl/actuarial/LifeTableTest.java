package com.mrffl.actuarial;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LifeTableTest {
    
    private static final double TOLERANCE = 1.0e-6;
    
    @Test
    public void testSurvivorsQxFormatFemale() {
        assertEquals(100000.0, LifeTable.survivors(0, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(99493.7, LifeTable.survivors(1, LifeTable.USSS_F_QX_DAT, 100000), 0.1);
        assertEquals(99348.0, LifeTable.survivors(10, LifeTable.USSS_F_QX_DAT, 100000), 1.0);
    }
    
    @Test
    public void testSurvivorsLxFormatFemale() {
        assertEquals(100000.0, LifeTable.survivors(0, LifeTable.USSS_F_LX_DAT, 0), TOLERANCE);
        assertEquals(99494.0, LifeTable.survivors(1, LifeTable.USSS_F_LX_DAT, 0), TOLERANCE);
        assertEquals(99348.0, LifeTable.survivors(10, LifeTable.USSS_F_LX_DAT, 0), TOLERANCE);
    }
    
    @Test
    public void testSurvivorsQxFormatMale() {
        assertEquals(100000.0, LifeTable.survivors(0, LifeTable.USSS_M_QX_DAT, 100000), TOLERANCE);
        assertEquals(99414.0, LifeTable.survivors(1, LifeTable.USSS_M_QX_DAT, 100000), 0.1);
        assertEquals(99238.0, LifeTable.survivors(10, LifeTable.USSS_M_QX_DAT, 100000), 1.0);
    }
    
    @Test
    public void testSurvivorsLxFormatMale() {
        assertEquals(100000.0, LifeTable.survivors(0, LifeTable.USSS_M_LX_DAT, 0), TOLERANCE);
        assertEquals(99414.0, LifeTable.survivors(1, LifeTable.USSS_M_LX_DAT, 0), TOLERANCE);
        assertEquals(99238.0, LifeTable.survivors(10, LifeTable.USSS_M_LX_DAT, 0), TOLERANCE);
    }
    
    @Test
    public void testSurvivorsEdgeCases() {
        assertEquals(100000.0, LifeTable.survivors(-1, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(0.0, LifeTable.survivors(120, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(0.0, LifeTable.survivors(1000, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
    }
    
    @Test
    public void testProbabilityOfDeathQxFormat() {
        assertEquals(LifeTable.USSS_F_QX_DAT[0], LifeTable.probabilityOfDeath(0, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(LifeTable.USSS_F_QX_DAT[1], LifeTable.probabilityOfDeath(1, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(LifeTable.USSS_F_QX_DAT[10], LifeTable.probabilityOfDeath(10, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
    }
    
    @Test
    public void testProbabilityOfDeathLxFormat() {
        double qx0 = LifeTable.probabilityOfDeath(0, LifeTable.USSS_F_LX_DAT, 0);
        assertEquals(0.00506, qx0, 0.00001);
        
        double qx1 = LifeTable.probabilityOfDeath(1, LifeTable.USSS_F_LX_DAT, 0);
        assertEquals(0.000392, qx1, 0.00001);
    }
    
    @Test
    public void testProbabilityOfDeathEdgeCases() {
        assertEquals(0.0, LifeTable.probabilityOfDeath(-1, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertTrue(LifeTable.probabilityOfDeath(119, LifeTable.USSS_F_QX_DAT, 100000) > 0.9);
        assertEquals(1.0, LifeTable.probabilityOfDeath(120, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
    }
    
    @Test
    public void testDied() {
        double died0 = LifeTable.died(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertEquals(506.3, died0, 1.0);
        
        double died1 = LifeTable.died(1, LifeTable.USSS_F_QX_DAT, 100000);
        assertEquals(39.1, died1, 0.5);
    }
    
    @Test
    public void testDiedEdgeCases() {
        assertEquals(0.0, LifeTable.died(-1, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
        assertEquals(0.0, LifeTable.died(120, LifeTable.USSS_F_QX_DAT, 100000), TOLERANCE);
    }
    
    @Test
    public void testLifeExpectancy() {
        double le0 = LifeTable.lifeExpectancy(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(le0 > 75.0 && le0 < 85.0);
        
        double le1 = LifeTable.lifeExpectancy(1, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(le1 > 75.0 && le1 < 85.0);
        
        double le10 = LifeTable.lifeExpectancy(10, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(le10 > 65.0 && le10 < 80.0);
    }
    
    @Test
    public void testLifeExpectancyAtBirth() {
        double leFemale = LifeTable.lifeExpectancyAtBirth(LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(leFemale > 79.0 && leFemale < 82.0);
        
        double leMale = LifeTable.lifeExpectancyAtBirth(LifeTable.USSS_M_QX_DAT, 100000);
        assertTrue(leMale > 73.0 && leMale < 77.0);
    }
    
    @Test
    public void testProbabilityOfSurvival1() {
        double ps1 = LifeTable.probabilityOfSurvival1(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertEquals(0.99494, ps1, 0.00001);
        
        double pd = LifeTable.probabilityOfDeath(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertEquals(1.0 - pd, ps1, 0.00001);
    }
    
    @Test
    public void testProbabilityOfSurvivalN() {
        double ps1_direct = LifeTable.probabilityOfSurvival1(0, LifeTable.USSS_F_QX_DAT, 100000);
        double ps1_via_n = LifeTable.probabilityOfSurvivalN(0, 1, LifeTable.USSS_F_QX_DAT, 100000);
        assertEquals(ps1_direct, ps1_via_n, 0.00001);
        
        double ps10 = LifeTable.probabilityOfSurvivalN(0, 10, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(ps10 > 0.99 && ps10 < 1.0);
    }
    
    @Test
    public void testMortalityRate() {
        double mr0 = LifeTable.mortalityRate(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(mr0 > 0.005 && mr0 < 0.006);
        
        double mr100 = LifeTable.mortalityRate(100, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(mr100 > 0.3);
    }
    
    @Test
    public void testPersonYears() {
        double py0 = LifeTable.personYears(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(py0 > 99000 && py0 < 100000);
        
        double py100 = LifeTable.personYears(100, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(py100 > 0);
    }
    
    @Test
    public void testTotalPersonYears() {
        double tpy0 = LifeTable.totalPersonYears(0, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(tpy0 > 7000000);
        
        double tpy100 = LifeTable.totalPersonYears(100, LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(tpy100 > 0 && tpy100 < 500000);
    }
    
    @Test
    public void testAgeAllDead() {
        int ageFemale = LifeTable.ageAllDead(LifeTable.USSS_F_QX_DAT, 100000);
        assertTrue(ageFemale >= 0 && ageFemale <= 120);
        
        int ageMale = LifeTable.ageAllDead(LifeTable.USSS_M_QX_DAT, 100000);
        assertTrue(ageMale >= 0 && ageMale <= 120);
    }
    
    @Test
    public void testRandAge() {
        for (int i = 0; i < 10; i++) {
            int age = LifeTable.randAge(0, LifeTable.USSS_F_QX_DAT, 100000);
            assertTrue(age >= 0 && age < 120);
        }
    }
    
    @Test
    public void testDataArrayLengths() {
        assertEquals(120, LifeTable.USSS_M_LX_DAT.length);
        assertEquals(120, LifeTable.USSS_F_LX_DAT.length);
        assertEquals(120, LifeTable.USSS_M_QX_DAT.length);
        assertEquals(120, LifeTable.USSS_F_QX_DAT.length);
        assertEquals(101, LifeTable.USCDC_W_F_LX_DAT.length);
        assertEquals(101, LifeTable.USCDC_W_M_LX_DAT.length);
        assertEquals(101, LifeTable.USCDC_W_LX_DAT.length);
        assertEquals(101, LifeTable.USCDC_LX_DAT.length);
    }
    
    @Test
    public void testBothFormatsProduceSimilarResults() {
        double survQx = LifeTable.survivors(10, LifeTable.USSS_F_QX_DAT, 100000);
        double survLx = LifeTable.survivors(10, LifeTable.USSS_F_LX_DAT, 0);
        assertEquals(survQx, survLx, 10.0);
        
        double pdQx = LifeTable.probabilityOfDeath(10, LifeTable.USSS_F_QX_DAT, 100000);
        double pdLx = LifeTable.probabilityOfDeath(10, LifeTable.USSS_F_LX_DAT, 0);
        assertEquals(pdQx, pdLx, 0.0001);
    }
}
