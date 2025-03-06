package com.neusoft.controller;

import com.neusoft.po.EfficientCreditVo;
import com.neusoft.po.ExpiredCreditVo;
import com.neusoft.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plus/CreditController")
@RefreshScope
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PostMapping("/addCreditByDateById")
    public int addCreditByDateById(@RequestParam("userId") String userId){
        return creditService.addCreditByDateById(userId);
    }

    @PostMapping("/credit2money")
    public int credit2money(@RequestParam("userId") String userId,@RequestParam("credit") Integer credit){
        return creditService.credit2money(userId,credit);
    }

    @GetMapping("/getCreditById")
    public int getCreditById(@RequestParam("userId") String userId){
        return creditService.getCreditById(userId);
    }

    @GetMapping("/listEfficientCreditById")
    public List<EfficientCreditVo> listEfficientCreditById(@RequestParam("userId") String userId,
                                                           @RequestParam("pageNum") Integer pageNum,
                                                           @RequestParam("pageSize") Integer pageSize){
        return creditService.listEfficientCreditById(userId,pageNum,pageSize);
    }

    @GetMapping("/listExpiredCreditById")
    public List<ExpiredCreditVo> listExpiredCreditById(@RequestParam("userId") String userId,
                                                       @RequestParam("pageNum") Integer pageNum,
                                                       @RequestParam("pageSize") Integer pageSize){
        return creditService.listExpiredCreditById(userId,pageNum,pageSize);
    }

    @GetMapping("/listEfficientGetCreditById")
    public List<EfficientCreditVo> listEfficientGetCreditById(@RequestParam("userId") String userId,
                                                              @RequestParam("pageNum") Integer pageNum,
                                                              @RequestParam("pageSize") Integer pageSize){
        return creditService.listEfficientGetCreditById(userId,pageNum,pageSize);
    }

    @GetMapping("/listCredit2MoneyById")
    public List<EfficientCreditVo> listCredit2MoneyById(@RequestParam("userId") String userId,
                                                        @RequestParam("pageNum") Integer pageNum,
                                                        @RequestParam("pageSize") Integer pageSize){
        return creditService.listCredit2MoneyById(userId,pageNum,pageSize);
    }
}
