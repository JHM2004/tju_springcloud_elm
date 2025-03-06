package com.neusoft.controller;

import com.neusoft.mapper.CreditMapper;
import com.neusoft.po.CommonResult;
import com.neusoft.po.WalletFlowDto;
import com.neusoft.po.WalletFlowVo;
import com.neusoft.service.VirtualWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/plus/VirtualWalletController")
@RefreshScope
public class VirtualWalletController {

    @Autowired
    private VirtualWalletService virtualWalletService;

    @Autowired
    private CreditMapper creditMapper;

    @GetMapping("/addWalletById")
    public CommonResult<Integer> addWalletById(@RequestParam("userId") String userId){
        int i =  virtualWalletService.addWalletById(userId);
        return new CommonResult(200,"success",i);
    }

    @GetMapping("/getWhetherEnabledById")
    public CommonResult<Integer> getWhetherEnabledById(@RequestParam("userId") String userId){
        int i =   virtualWalletService.getWhetherEnabledById(userId);
        return new CommonResult(200,"success",i);
    }

    @PostMapping("/rechargeById")
    @CachePut(cacheNames = "getCreditById", key = "#walletFlowDto.getUserId()")
    public CommonResult<Integer> rechargeById(@RequestBody WalletFlowDto walletFlowDto){
        virtualWalletService.rechargeById(walletFlowDto);
        int i =   creditMapper.getCreditAllById(walletFlowDto.getUserId());
        return new CommonResult(200,"success",i);
    }

    @PostMapping("/withdrawById")
    public CommonResult<BigDecimal> withdrawById(@RequestBody WalletFlowDto walletFlowDto){
        BigDecimal result = BigDecimal.valueOf(0);
        try {
            result = virtualWalletService.withdrawById(walletFlowDto);
        } catch (Exception e) {
            return new CommonResult(200,"success",BigDecimal.valueOf(0));
        }
        return new CommonResult(200,"success",result);
    }

    @PostMapping("/pay")
    @CachePut(cacheNames = "getCreditById", key = "#userId")
    public CommonResult<Integer> pay(@RequestBody WalletFlowDto walletFlowDto){
        try {
            virtualWalletService.pay(walletFlowDto);
        } catch (Exception e) {
            return new CommonResult(200,"success",0);
        }
        int i =   creditMapper.getCreditAllById(walletFlowDto.getUserId());
        return new CommonResult(200,"success",i);
    }

    @GetMapping("/getBalanceById")
    public CommonResult<BigDecimal> getBalanceById(@RequestParam String userId){
        BigDecimal b =  virtualWalletService.getBalanceById(userId);
        return new CommonResult(200,"success",b);
    }

    @GetMapping("/listWalletLogById")
    public CommonResult<List> listWalletLogById(@RequestParam String userId){
        List<WalletFlowVo> walletFlowVos = virtualWalletService.listWalletLogById(userId);
        return new CommonResult(200,"success",walletFlowVos);
    }

    @GetMapping("/listWalletLogIncomeById")
    public CommonResult<List> listWalletLogIncomeById(@RequestParam String userId){
        List<WalletFlowVo> walletFlowVos = virtualWalletService.listWalletLogIncomeById(userId);
        return new CommonResult(200,"success",walletFlowVos);
    }

    @GetMapping("/listWalletLogOutcomeById")
    public CommonResult<List> listWalletLogOutcomeById(@RequestParam String userId){
        List<WalletFlowVo> walletFlowVos = virtualWalletService.listWalletLogOutcomeById(userId);
        return new CommonResult(200,"success",walletFlowVos);
    }

    @GetMapping("/getFreeze")
    public CommonResult<Integer> getFreeze(@RequestParam String userId){
        int i =   virtualWalletService.getFreeze(userId);
        return new CommonResult(200,"success",i);
    }

    @PostMapping("/freeze")
    public CommonResult<Integer> freeze(@RequestParam String userId){
        int i =  virtualWalletService.freeze(userId);
        return new CommonResult(200,"success",i);
    }

    @PostMapping("/unFreeze")
    public CommonResult<Integer> unFreeze(@RequestParam String userId){
        int i  = virtualWalletService.unFreeze(userId);
        return new CommonResult(200,"success",i);
    }

    @PutMapping("/rechargeByIdByCredit/{userId}/{credit}")
    public CommonResult rechargeByIdByCredit(
            @PathVariable("userId") String userId,
            @PathVariable("credit") Integer credit
    ) throws Exception{
        virtualWalletService.rechargeByIdByCredit(userId, credit);
        return new CommonResult(200,"success",null);
    }
}
