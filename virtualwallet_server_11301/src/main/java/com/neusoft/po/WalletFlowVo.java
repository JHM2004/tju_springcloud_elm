package com.neusoft.po;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletFlowVo {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    private String userId;

    public WalletFlowVo(Integer id, String userId, Integer targetType, String targetId, BigDecimal fee, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.fee = fee;
        this.createTime = createTime;
    }

    private Integer targetType;

    public WalletFlowVo() {
    }

    private String targetId;
    private BigDecimal fee;
    private LocalDateTime createTime;
}
