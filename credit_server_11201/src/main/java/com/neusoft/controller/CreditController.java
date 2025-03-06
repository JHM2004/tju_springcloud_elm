package com.neusoft.controller;

import com.neusoft.po.CommonResult;
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
    public CommonResult<Integer> addCreditByDateById(@RequestParam("userId") String userId) {
        int i = creditService.addCreditByDateById(userId);
        return new CommonResult(200, "success", i);
    }

    @PostMapping("/credit2money")
    public CommonResult<Integer> credit2money(@RequestParam("userId") String userId, @RequestParam("credit") Integer credit) {
        int i = creditService.credit2money(userId, credit);
        return new CommonResult(200, "success", i);
    }

    @GetMapping("/getCreditById")
    public CommonResult<Integer> getCreditById(@RequestParam("userId") String userId) {
        int i = creditService.getCreditById(userId);
        return new CommonResult(200, "success", i);
    }

    @GetMapping("/listEfficientCreditById")
    public CommonResult<List> listEfficientCreditById(@RequestParam("userId") String userId,
                                                      @RequestParam("pageNum") Integer pageNum,
                                                      @RequestParam("pageSize") Integer pageSize) {
        List<EfficientCreditVo> efficientCreditVos = creditService.listEfficientCreditById(userId, pageNum, pageSize);
        return new CommonResult(200, "success", efficientCreditVos);
    }

    @GetMapping("/listExpiredCreditById")
    public CommonResult<List> listExpiredCreditById(@RequestParam("userId") String userId,
                                                    @RequestParam("pageNum") Integer pageNum,
                                                    @RequestParam("pageSize") Integer pageSize) {
        List<ExpiredCreditVo> expiredCreditVos = creditService.listExpiredCreditById(userId, pageNum, pageSize);
        return new CommonResult(200, "success", expiredCreditVos);
    }

    @GetMapping("/listEfficientGetCreditById")
    public CommonResult<List> listEfficientGetCreditById(@RequestParam("userId") String userId,
                                                         @RequestParam("pageNum") Integer pageNum,
                                                         @RequestParam("pageSize") Integer pageSize) {
        List<EfficientCreditVo> efficientCreditVos = creditService.listEfficientGetCreditById(userId, pageNum, pageSize);
        return new CommonResult(200, "success", efficientCreditVos);
    }

    @GetMapping("/listCredit2MoneyById")
    public CommonResult<List> listCredit2MoneyById(@RequestParam("userId") String userId,
                                                   @RequestParam("pageNum") Integer pageNum,
                                                   @RequestParam("pageSize") Integer pageSize) {
        List<EfficientCreditVo> efficientCreditVos = creditService.listCredit2MoneyById(userId, pageNum, pageSize);
        return new CommonResult(200, "success", efficientCreditVos);
    }
}
