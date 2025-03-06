package com.neusoft.po;

import java.math.BigDecimal;

public class WalletFlowDto{

    private String userId;

    public WalletFlowDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public WalletFlowDto(String userId, BigDecimal amount, String targetId) {
        this.userId = userId;
        this.amount = amount;
        this.targetId = targetId;
    }

    private BigDecimal amount;

    private String targetId;
}
