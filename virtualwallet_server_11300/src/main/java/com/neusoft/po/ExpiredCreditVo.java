package com.neusoft.po;


import java.time.LocalDateTime;

public class ExpiredCreditVo {
    private Integer id;
    private String userId;

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

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public int getEfficient() {
        return efficient;
    }

    public void setEfficient(int efficient) {
        this.efficient = efficient;
    }

    public ExpiredCreditVo() {
    }

    public ExpiredCreditVo(Integer id, String userId, Integer channelId, Integer typeId, Integer credit, LocalDateTime createTime, LocalDateTime expiredTime, int efficient) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.typeId = typeId;
        this.credit = credit;
        this.createTime = createTime;
        this.expiredTime = expiredTime;
        this.efficient = efficient;
    }

    private Integer channelId;
    private Integer typeId;
    private Integer credit;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;
    private int efficient;
}
