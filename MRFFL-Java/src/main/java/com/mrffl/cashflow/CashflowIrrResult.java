package com.mrffl.cashflow;

public class CashflowIrrResult {
    private final double irr;
    private final int status;
    
    public CashflowIrrResult(double irr, int status) {
        this.irr = irr;
        this.status = status;
    }
    
    public double getIrr() {
        return irr;
    }
    
    public int getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == 0;
    }
    
    @Override
    public String toString() {
        return String.format("CashflowIrrResult{irr=%.8f, status=%d}", irr, status);
    }
}
