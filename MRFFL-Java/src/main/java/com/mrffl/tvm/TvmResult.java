package com.mrffl.tvm;

public class TvmResult {
    private final double n;
    private final double i;
    private final double pv;
    private final double fv;
    private final double a;
    private final double g;
    private final double q;
    private final int status;
    
    public TvmResult(double n, double i, double pv, double fv, double a, double g, double q, int status) {
        this.n = n;
        this.i = i;
        this.pv = pv;
        this.fv = fv;
        this.a = a;
        this.g = g;
        this.q = q;
        this.status = status;
    }
    
    public double getN() {
        return n;
    }
    
    public double getI() {
        return i;
    }
    
    public double getPv() {
        return pv;
    }
    
    public double getFv() {
        return fv;
    }
    
    public double getA() {
        return a;
    }
    
    public double getG() {
        return g;
    }
    
    public double getQ() {
        return q;
    }
    
    public int getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == 0;
    }
    
    @Override
    public String toString() {
        return String.format("TvmResult{n=%.4f, i=%.4f, pv=%.4f, fv=%.4f, a=%.4f, g=%.4f, q=%.4f, status=%d}", 
                             n, i, pv, fv, a, g, q, status);
    }
}
