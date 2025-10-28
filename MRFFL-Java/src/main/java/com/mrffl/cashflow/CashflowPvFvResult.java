package com.mrffl.cashflow;

public class CashflowPvFvResult {
    private final double[] pvVec;
    private final double[] fvVec;
    private final int status;
    
    public CashflowPvFvResult(double[] pvVec, double[] fvVec, int status) {
        this.pvVec = pvVec;
        this.fvVec = fvVec;
        this.status = status;
    }
    
    public double[] getPvVec() {
        return pvVec;
    }
    
    public double[] getFvVec() {
        return fvVec;
    }
    
    public int getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == 0;
    }
    
    @Override
    public String toString() {
        return String.format("CashflowPvFvResult{pvVec.length=%d, fvVec.length=%d, status=%d}", 
                             pvVec.length, fvVec.length, status);
    }
}
