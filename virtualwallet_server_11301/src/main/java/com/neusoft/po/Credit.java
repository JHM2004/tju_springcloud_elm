package com.neusoft.po;


public class Credit {
    private Integer id;

    private String userId;

    public Credit() {
    }

    private Integer credit_all;

    public Credit(Integer id, String userId, Integer credit_all) {
        this.id = id;
        this.userId = userId;
        this.credit_all = credit_all;
    }

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

    public Integer getCredit_all() {
        return credit_all;
    }

    public void setCredit_all(Integer credit_all) {
        this.credit_all = credit_all;
    }
}
