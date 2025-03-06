package com.neusoft.po;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;

@Component
public class MessageMoneyDto implements Serializable {
    private static final long serialVersionUID = 1L; // 定义序列化ID

    private String userId;

    public MessageMoneyDto() {
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

    public MessageMoneyDto(String userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    private BigDecimal amount;

}
