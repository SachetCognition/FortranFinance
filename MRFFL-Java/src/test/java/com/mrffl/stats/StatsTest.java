package com.mrffl.stats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for statistical functions.
 */
public class StatsTest {
    
    private static final double TOLERANCE = 1e-6;
    
    @Test
    public void testRandIntBounds() {
        for (int i = 0; i < 100; i++) {
            int value = Stats.randInt(10);
            assertTrue(value >= 0 && value < 10);
        }
    }
    
    @Test
    public void testRandIntWithLowerBound() {
        for (int i = 0; i < 100; i++) {
            int value = Stats.randInt(20, 10);
            assertTrue(value >= 10 && value < 20);
        }
    }
    
    @Test
    public void testRandRealBounds() {
        for (int i = 0; i < 100; i++) {
            double value = Stats.randReal(10.0);
            assertTrue(value >= 0.0 && value < 10.0);
        }
    }
    
    @Test
    public void testRandRealWithLowerBound() {
        for (int i = 0; i < 100; i++) {
            double value = Stats.randReal(20.0, 10.0);
            assertTrue(value >= 10.0 && value < 20.0);
        }
    }
    
    @Test
    public void testResampleTail() {
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        
        for (int i = 0; i < 50; i++) {
            double value = Stats.resampleTail(data, 3);
            assertTrue(value >= 8.0 && value <= 10.0);
        }
    }
    
    @Test
    public void testResampleHead() {
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        
        for (int i = 0; i < 50; i++) {
            double value = Stats.resampleHead(data, 3);
            assertTrue(value >= 1.0 && value <= 3.0);
        }
    }
    
    @Test
    public void testMeanAndVariance() {
        double[] data = {2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0};
        double[] result = Stats.meanAndVariance(data);
        
        assertEquals(5.0, result[0], TOLERANCE);
        assertEquals(4.0, result[1], TOLERANCE);
    }
    
    @Test
    public void testMeanAndVarianceEmptyArray() {
        double[] data = {};
        double[] result = Stats.meanAndVariance(data);
        
        assertEquals(0.0, result[0], TOLERANCE);
        assertEquals(0.0, result[1], TOLERANCE);
    }
    
    @Test
    public void testMeanAndVarianceSingleElement() {
        double[] data = {5.0};
        double[] result = Stats.meanAndVariance(data);
        
        assertEquals(5.0, result[0], TOLERANCE);
        assertEquals(0.0, result[1], TOLERANCE);
    }
    
    @Test
    public void testRandNormStdDistribution() {
        int n = 10000;
        double sum = 0.0;
        double sumSq = 0.0;
        
        for (int i = 0; i < n; i++) {
            double value = Stats.randNormStd();
            sum += value;
            sumSq += value * value;
        }
        
        double mean = sum / n;
        double variance = (sumSq - sum * sum / n) / (n - 1);
        
        assertEquals(0.0, mean, 0.05);
        assertEquals(1.0, variance, 0.05);
    }
    
    @Test
    public void testRandNormStdBoxDistribution() {
        int n = 10000;
        double sum = 0.0;
        double sumSq = 0.0;
        
        for (int i = 0; i < n; i++) {
            double value = Stats.randNormStdBox();
            sum += value;
            sumSq += value * value;
        }
        
        double mean = sum / n;
        double variance = (sumSq - sum * sum / n) / (n - 1);
        
        assertEquals(0.0, mean, 0.05);
        assertEquals(1.0, variance, 0.05);
    }
    
    @Test
    public void testRandNorm() {
        int n = 10000;
        double targetMean = 10.0;
        double targetVariance = 4.0;
        double sum = 0.0;
        double sumSq = 0.0;
        
        for (int i = 0; i < n; i++) {
            double value = Stats.randNorm(targetMean, targetVariance);
            sum += value;
            sumSq += value * value;
        }
        
        double mean = sum / n;
        double variance = (sumSq - sum * sum / n) / (n - 1);
        
        assertEquals(targetMean, mean, 0.1);
        assertEquals(targetVariance, variance, 0.2);
    }
    
    @Test
    public void testRandLogNorm() {
        for (int i = 0; i < 100; i++) {
            double value = Stats.randLogNorm(0.0, 1.0);
            assertTrue(value > 0.0);
        }
    }
    
    @Test
    public void testProbitKnownValues() {
        assertEquals(0.0, Stats.probit(0.5), TOLERANCE);
        assertEquals(-1.0, Stats.probit(0.1586552539314570), 0.001);
        assertEquals(1.0, Stats.probit(0.8413447460685429), 0.001);
        assertEquals(-2.0, Stats.probit(0.0227501319481792), 0.001);
        assertEquals(2.0, Stats.probit(0.9772498680518208), 0.001);
    }
    
    @Test
    public void testProbitBoundaryValues() {
        assertThrows(IllegalArgumentException.class, () -> Stats.probit(0.0));
        assertThrows(IllegalArgumentException.class, () -> Stats.probit(1.0));
        assertThrows(IllegalArgumentException.class, () -> Stats.probit(-0.1));
        assertThrows(IllegalArgumentException.class, () -> Stats.probit(1.1));
    }
    
    @Test
    public void testProbitSymmetry() {
        double p1 = 0.3;
        double p2 = 0.7;
        double z1 = Stats.probit(p1);
        double z2 = Stats.probit(p2);
        
        assertEquals(-z1, z2, TOLERANCE);
    }
    
    @Test
    public void testGeometricBrownianMotion() {
        double[] steps = new double[100];
        double s0 = 100.0;
        double mu = 0.05;
        double sigma = 0.2;
        
        Stats.geometricBrownianMotion(steps, s0, mu, sigma);
        
        assertEquals(s0, steps[0], TOLERANCE);
        assertEquals(100, steps.length);
        
        for (int i = 0; i < steps.length; i++) {
            assertTrue(steps[i] > 0.0);
        }
    }
    
    @Test
    public void testZeroClippedBrownianMotion() {
        double[] steps = new double[100];
        double s0 = 100.0;
        double mu = -0.5;
        double sigma = 0.8;
        
        Stats.zeroClippedBrownianMotion(steps, s0, mu, sigma);
        
        assertEquals(s0, steps[0], TOLERANCE);
        assertEquals(100, steps.length);
        
        for (int i = 0; i < steps.length; i++) {
            assertTrue(steps[i] >= 0.0);
        }
    }
    
    @Test
    public void testGeometricBrownianMotionEmptyArray() {
        double[] steps = new double[0];
        Stats.geometricBrownianMotion(steps, 100.0, 0.05, 0.2);
    }
    
    @Test
    public void testRandNormStdProbitClip() {
        for (int i = 0; i < 100; i++) {
            double value = Stats.randNormStdProbitClip();
            assertTrue(value > -10.0 && value < 10.0);
        }
    }
}
