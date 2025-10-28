package com.mrffl.tvm;

public class Tvm12Result {
    private final int n;
    private final double i;
    private final double pv;
    private final double pmt;
    private final double fv;
    private final int status;
    
    public Tvm12Result(int n, double i, double pv, double pmt, double fv, int status) {
        this.n = n;
        this.i = i;
        this.pv = pv;
        this.pmt = pmt;
        this.fv = fv;
        this.status = status;
    }
    
    public int getN() {
        return n;
    }
    
    public double getI() {
        return i;
    }
    
    public double getPv() {
        return pv;
    }
    
    public double getPmt() {
        return pmt;
    }
    
    public double getFv() {
        return fv;
    }
    
    public int getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == 0;
    }
    
    @Override
    public String toString() {
        return String.format("Tvm12Result{n=%d, i=%.8f, pv=%.2f, pmt=%.2f, fv=%.2f, status=%d}",
                n, i, pv, pmt, fv, status);
    }
}
