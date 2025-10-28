package com.mrffl.stats;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Statistical utilities for random number generation, distributions, and
 * stochastic processes.
 * 
 * This module provides functions for:
 * - Uniform random number generation (integers and reals)
 * - Data resampling (head and tail)
 * - Normal and log-normal distributions
 * - Probit function (inverse standard normal CDF)
 * - Brownian motion simulations
 * - Basic statistical calculations
 * 
 * Corresponds to MRFFL/src/mrffl_stats.f90
 * 
 * @author MRFFL Java Migration
 * @version 1.0.0
 */
public final class Stats {
    
    private static final ThreadLocal<Double> boxMullerCache = 
        ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Boolean> boxMullerHasCache = 
        ThreadLocal.withInitial(() -> false);
    
    /**
     * Generate a random integer in the range [lowerBound, upperBound).
     * 
     * @param upperBound exclusive upper bound
     * @param lowerBound inclusive lower bound
     * @return random integer in [lowerBound, upperBound)
     */
    public static int randInt(int upperBound, int lowerBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }
    
    /**
     * Generate a random integer in the range [0, upperBound).
     * 
     * @param upperBound exclusive upper bound
     * @return random integer in [0, upperBound)
     */
    public static int randInt(int upperBound) {
        return randInt(upperBound, 0);
    }
    
    /**
     * Generate a random real number in the range [lowerBound, upperBound).
     * 
     * @param upperBound exclusive upper bound
     * @param lowerBound inclusive lower bound
     * @return random double in [lowerBound, upperBound)
     */
    public static double randReal(double upperBound, double lowerBound) {
        double u = ThreadLocalRandom.current().nextDouble();
        return lowerBound + u * (upperBound - lowerBound);
    }
    
    /**
     * Generate a random real number in the range [0, upperBound).
     * 
     * @param upperBound exclusive upper bound
     * @return random double in [0, upperBound)
     */
    public static double randReal(double upperBound) {
        return randReal(upperBound, 0.0);
    }
    
    /**
     * Resample a random value from the tail (last N elements) of an array.
     * 
     * @param data the data array to sample from
     * @param tailLength number of elements from the end to consider
     * @return random value from the last tailLength elements
     */
    public static double resampleTail(double[] data, int tailLength) {
        int n = data.length;
        int startIdx = Math.max(0, n - tailLength);
        int idx = randInt(n, startIdx);
        return data[idx];
    }
    
    /**
     * Resample a random value from the head (first N elements) of an array.
     * 
     * @param data the data array to sample from
     * @param headLength number of elements from the beginning to consider
     * @return random value from the first headLength elements
     */
    public static double resampleHead(double[] data, int headLength) {
        int endIdx = Math.min(data.length, headLength);
        int idx = randInt(endIdx);
        return data[idx];
    }
    
    /**
     * Compute mean and variance of a data array.
     * 
     * Note: This returns the population variance (divides by n), matching
     * the Fortran implementation which divides sum of squares by array size.
     * 
     * @param data the data array
     * @return array [mean, variance] where variance is the population variance
     */
    public static double[] meanAndVariance(double[] data) {
        int n = data.length;
        if (n == 0) {
            return new double[]{0.0, 0.0};
        }
        
        double sum = 0.0;
        for (double x : data) {
            sum += x;
        }
        double mean = sum / n;
        
        double sumSq = 0.0;
        for (double x : data) {
            double diff = x - mean;
            sumSq += diff * diff;
        }
        double variance = sumSq / n;
        
        return new double[]{mean, variance};
    }
    
    /**
     * Generate a standard normal random variable using the Box-Muller transform.
     * 
     * This method uses caching to avoid wasting the second value generated
     * by the Box-Muller transform.
     * 
     * @return random value from N(0, 1)
     */
    public static double randNormStdBox() {
        if (boxMullerHasCache.get()) {
            boxMullerHasCache.set(false);
            return boxMullerCache.get();
        }
        
        double u1 = ThreadLocalRandom.current().nextDouble();
        double u2 = ThreadLocalRandom.current().nextDouble();
        
        double r = Math.sqrt(-2.0 * Math.log(u1));
        double theta = 2.0 * Math.PI * u2;
        
        double z0 = r * Math.cos(theta);
        double z1 = r * Math.sin(theta);
        
        boxMullerCache.set(z1);
        boxMullerHasCache.set(true);
        
        return z0;
    }
    
    /**
     * Generate a standard normal random variable using the probit function.
     * 
     * @return random value from N(0, 1)
     */
    public static double randNormStdProbit() {
        double u = ThreadLocalRandom.current().nextDouble();
        return probit(u);
    }
    
    /**
     * Generate a standard normal random variable using the probit function
     * with clipping to avoid extreme values.
     * 
     * The clipping bounds correspond to approximately ±5.612 standard deviations,
     * which covers more than 99.9999% of the normal distribution.
     * 
     * @return random value from N(0, 1), clipped to reasonable bounds
     */
    public static double randNormStdProbitClip() {
        double u = ThreadLocalRandom.current().nextDouble();
        u = Math.max(1.0e-7, Math.min(1.0 - 1.0e-7, u));
        return probit(u);
    }
    
    /**
     * Generate a standard normal random variable.
     * 
     * This is the default method, which delegates to randNormStdProbit().
     * 
     * @return random value from N(0, 1)
     */
    public static double randNormStd() {
        return randNormStdProbit();
    }
    
    /**
     * Generate a normal random variable with specified mean and variance.
     * 
     * @param mean the mean of the distribution
     * @param variance the variance of the distribution
     * @return random value from N(mean, variance)
     */
    public static double randNorm(double mean, double variance) {
        double z = randNormStd();
        return mean + Math.sqrt(variance) * z;
    }
    
    /**
     * Generate a log-normal random variable.
     * 
     * If X ~ N(mu, sigma^2), then exp(X) ~ LogNormal(mu, sigma^2).
     * 
     * @param mu the mean parameter of the underlying normal distribution
     * @param sigma the standard deviation parameter of the underlying normal
     * @return random value from LogNormal(mu, sigma^2)
     */
    public static double randLogNorm(double mu, double sigma) {
        double z = randNormStd();
        return Math.exp(mu + sigma * z);
    }
    
    /**
     * Compute the probit function (inverse of the standard normal CDF).
     * 
     * This implements Wichura's Algorithm AS 241, which provides high accuracy
     * across the entire range (0, 1). The algorithm uses rational function
     * approximations in three different regions for optimal precision.
     * 
     * Reference: Wichura, M. J. (1988). Algorithm AS 241: The Percentage Points
     * of the Normal Distribution. Applied Statistics, 37(3), 477-484.
     * 
     * @param p probability in (0, 1)
     * @return the value z such that Φ(z) = p, where Φ is the standard normal CDF
     */
    public static double probit(double p) {
        if (p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("p must be in (0, 1), got: " + p);
        }
        
        double q = p - 0.5;
        
        if (Math.abs(q) <= 0.425) {
            double r = 0.180625 - q * q;
            double[] a = {
                3.3871328727963666080e0,
                1.3314166789178437745e2,
                1.9715909503065514427e3,
                1.3731693765509461125e4,
                4.5921953931549871457e4,
                6.7265770927008700853e4,
                3.3430575583588128105e4,
                2.5090809287301226727e3
            };
            double[] b = {
                1.0,
                4.2313323607431962938e1,
                6.8718700749205790830e2,
                5.3941960214247511077e3,
                2.1213794301586595867e4,
                3.9307895800092710610e4,
                2.8729085735721942674e4,
                5.2264952788528545610e3
            };
            
            double num = polyEval(a, r);
            double den = polyEval(b, r);
            return q * num / den;
        }
        
        double r = (q < 0.0) ? p : (1.0 - p);
        r = Math.sqrt(-Math.log(r));
        
        double num, den;
        if (r <= 5.0) {
            r = r - 1.6;
            double[] c = {
                1.42343711074968357734e0,
                4.63033784615654529590e0,
                5.76949722146069140550e0,
                3.64784832476320460504e0,
                1.27045825245236838258e0,
                2.41780725177450611770e-1,
                2.27238449892691845833e-2,
                7.74545014278341407640e-4
            };
            double[] d = {
                1.0,
                2.05319162663775882187e0,
                1.67638483018380384940e0,
                6.89767334985100004550e-1,
                1.48103976427480074590e-1,
                1.51986665636164571966e-2,
                5.47593808499534494600e-4,
                1.05075007164441684324e-9
            };
            num = polyEval(c, r);
            den = polyEval(d, r);
        } else {
            r = r - 5.0;
            double[] e = {
                6.65790464350110377720e0,
                5.46378491116411436990e0,
                1.78482653991729133580e0,
                2.96560571828504891230e-1,
                2.65321895265761230930e-2,
                1.24266094738807843860e-3,
                2.71155556874348757815e-5,
                2.01033439929228813265e-7
            };
            double[] f = {
                1.0,
                5.99832206555887937690e-1,
                1.36929880922735805310e-1,
                1.48753612908506148525e-2,
                7.86869131145613259100e-4,
                1.84631831751005468180e-5,
                1.42151175831644588870e-7,
                2.04426310338993978564e-15
            };
            num = polyEval(e, r);
            den = polyEval(f, r);
        }
        
        double z = num / den;
        return (q < 0.0) ? -z : z;
    }
    
    /**
     * Evaluate a polynomial using Horner's method.
     * 
     * @param coeffs polynomial coefficients [c0, c1, c2, ..., cn]
     * @param x the value at which to evaluate the polynomial
     * @return c0 + c1*x + c2*x^2 + ... + cn*x^n
     */
    private static double polyEval(double[] coeffs, double x) {
        double result = coeffs[coeffs.length - 1];
        for (int i = coeffs.length - 2; i >= 0; i--) {
            result = result * x + coeffs[i];
        }
        return result;
    }
    
    /**
     * Simulate a geometric Brownian motion process.
     * 
     * Geometric Brownian motion is commonly used to model stock prices and
     * other financial assets. The process is defined by:
     * dS = μ*S*dt + σ*S*dW
     * 
     * @param stepValues output array to store the simulated path
     * @param s0 initial value
     * @param mu drift parameter (expected return)
     * @param sigma volatility parameter (standard deviation)
     */
    public static void geometricBrownianMotion(double[] stepValues, 
                                              double s0, double mu, double sigma) {
        int n = stepValues.length;
        if (n == 0) return;
        
        stepValues[0] = s0;
        double dt = 1.0;
        double sqrtDt = Math.sqrt(dt);
        
        for (int i = 1; i < n; i++) {
            double z = randNormStd();
            double drift = (mu - 0.5 * sigma * sigma) * dt;
            double diffusion = sigma * sqrtDt * z;
            stepValues[i] = stepValues[i - 1] * Math.exp(drift + diffusion);
        }
    }
    
    /**
     * Simulate a zero-clipped Brownian motion process.
     * 
     * This is similar to geometric Brownian motion, but values are clipped
     * to be non-negative. When the process would go below zero, it is
     * reflected back to zero.
     * 
     * @param stepValues output array to store the simulated path
     * @param s0 initial value
     * @param mu drift parameter
     * @param sigma volatility parameter
     */
    public static void zeroClippedBrownianMotion(double[] stepValues,
                                                double s0, double mu, double sigma) {
        int n = stepValues.length;
        if (n == 0) return;
        
        stepValues[0] = s0;
        double dt = 1.0;
        double sqrtDt = Math.sqrt(dt);
        
        for (int i = 1; i < n; i++) {
            double z = randNormStd();
            double drift = (mu - 0.5 * sigma * sigma) * dt;
            double diffusion = sigma * sqrtDt * z;
            double newValue = stepValues[i - 1] * Math.exp(drift + diffusion);
            stepValues[i] = Math.max(0.0, newValue);
        }
    }
    
    private Stats() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
