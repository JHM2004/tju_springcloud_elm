package com.neusoft.service;

import com.neusoft.po.EfficientCreditVo;
import com.neusoft.po.ExpiredCreditVo;

import java.util.List;

public interface CreditService {
    int addCreditByDateById(String userId);

    int credit2money(String userId, Integer credit);

    int getCreditById(String userId);

    List<EfficientCreditVo> listEfficientCreditById(String userId , Integer page, Integer pageSize);

    List<ExpiredCreditVo> listExpiredCreditById(String userId , Integer page, Integer pageSize);

    List<EfficientCreditVo> listEfficientGetCreditById(String userId , Integer page, Integer pageSize);

    List<EfficientCreditVo> listCredit2MoneyById(String userId , Integer page, Integer pageSize);
}
