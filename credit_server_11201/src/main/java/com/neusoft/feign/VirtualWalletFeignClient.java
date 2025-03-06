package com.neusoft.feign;

import com.neusoft.po.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="virtualwallet-server",fallback= VirtualWalletFeignClientCallBack.class)
public interface VirtualWalletFeignClient {

    @PutMapping("/rechargeByIdByCredit/{userId}/{credit}")
    public CommonResult rechargeByIdByCredit(
            @PathVariable("userId") String userId,
            @PathVariable("credit") Integer credit
    );
}
