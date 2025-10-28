package com.mrffl.cashflow;

public class CashflowVectorResult {
    private final double[] cfVec;
    private final int status;
    
    public CashflowVectorResult(double[] cfVec, int status) {
        this.cfVec = cfVec;
        this.status = status;
    }
    
    public double[] getCfVec() {
        return cfVec;
    }
    
    public int getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == 0;
    }
    
    @Override
    public String toString() {
        return String.format("CashflowVectorResult{cfVec.length=%d, status=%d}", cfVec.length, status);
    }
}
