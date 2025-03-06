package com.neusoft.feign;

import com.neusoft.po.CommonResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class VirtualWalletFeignClientCallBack implements VirtualWalletFeignClient {

    @Override
   public CommonResult rechargeByIdByCredit(
            @PathVariable("userId") String userId,
            @PathVariable("credit") Integer credit
    ){
        //返回降级相应 , 食品信息返回null
        return new CommonResult(403,"feign触发了熔断降级",null);
    }
}