package com.neusoft.po;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Component
public class UserWallet {

    private String userId;

    public UserWallet() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public LocalDateTime getUpdateLast() {
        return updateLast;
    }

    public void setUpdateLast(LocalDateTime updateLast) {
        this.updateLast = updateLast;
    }

    public UserWallet(String userId, BigDecimal balance, String freeze, LocalDateTime updateLast) {
        this.userId = userId;
        this.balance = balance;
        this.freeze = freeze;
        this.updateLast = updateLast;
    }

    private BigDecimal balance;

    private String freeze;

    private LocalDateTime updateLast;

    public void unFreeze(){
        this.setFreeze("1");
    }
}
