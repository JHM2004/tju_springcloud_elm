package com.neusoft.controller;

import com.neusoft.mapper.CreditMapper;
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
    public int addWalletById(@RequestParam("userId") String userId){
        return virtualWalletService.addWalletById(userId);
    }

    @GetMapping("/getWhetherEnabledById")
    public int getWhetherEnabledById(@RequestParam("userId") String userId){
        return virtualWalletService.getWhetherEnabledById(userId);
    }

    @PostMapping("/rechargeById")
    @CachePut(cacheNames = "getCreditById", key = "#walletFlowDto.getUserId()")
    public int rechargeById(@RequestBody WalletFlowDto walletFlowDto){
        virtualWalletService.rechargeById(walletFlowDto);
        return creditMapper.getCreditAllById(walletFlowDto.getUserId());
    }

    @PostMapping("/withdrawById")
    public BigDecimal withdrawById(@RequestBody WalletFlowDto walletFlowDto){
        BigDecimal result = BigDecimal.valueOf(0);
        try {
            result = virtualWalletService.withdrawById(walletFlowDto);
        } catch (Exception e) {
            return BigDecimal.valueOf(0);
        }
        return result;
    }

    @PostMapping("/pay")
    @CachePut(cacheNames = "getCreditById", key = "#userId")
    public int pay(@RequestBody WalletFlowDto walletFlowDto){
        try {
            virtualWalletService.pay(walletFlowDto);
        } catch (Exception e) {
            return 0;
        }
        return creditMapper.getCreditAllById(walletFlowDto.getUserId());
    }

    @GetMapping("/getBalanceById")
    public BigDecimal getBalanceById(@RequestParam String userId){
        return virtualWalletService.getBalanceById(userId);
    }

    @GetMapping("/listWalletLogById")
    public List<WalletFlowVo> listWalletLogById(@RequestParam String userId){
        return virtualWalletService.listWalletLogById(userId);
    }

    @GetMapping("/listWalletLogIncomeById")
    public List<WalletFlowVo> listWalletLogIncomeById(@RequestParam String userId){
        return virtualWalletService.listWalletLogIncomeById(userId);
    }

    @GetMapping("/listWalletLogOutcomeById")
    public List<WalletFlowVo> listWalletLogOutcomeById(@RequestParam String userId){
        return virtualWalletService.listWalletLogOutcomeById(userId);
    }

    @GetMapping("/getFreeze")
    public int getFreeze(@RequestParam String userId){
        return virtualWalletService.getFreeze(userId);
    }

    @PostMapping("/freeze")
    public int freeze(@RequestParam String userId){
        return virtualWalletService.freeze(userId);
    }

    @PostMapping("/unFreeze")
    public int unFreeze(@RequestParam String userId){
        return virtualWalletService.unFreeze(userId);
    }

    @PutMapping("/rechargeByIdByCredit/{userId}/{credit}")
    void rechargeByIdByCredit(
        @PathVariable("userId") String userId,
        @PathVariable("credit") Integer credit
    ) throws Exception{
        virtualWalletService.rechargeByIdByCredit(userId, credit);
    }
}
