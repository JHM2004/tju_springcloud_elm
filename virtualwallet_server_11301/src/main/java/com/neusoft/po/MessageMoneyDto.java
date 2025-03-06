package com.neusoft.po;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;

@Component
public class MessageMoneyDto implements Serializable {
    private static final long serialVersionUID = 1L; // 定义序列化ID

    private String userId;

    private BigDecimal amount;

}
