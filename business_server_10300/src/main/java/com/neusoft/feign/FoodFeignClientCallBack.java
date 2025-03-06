package com.neusoft.feign;

import com.neusoft.po.CommonResult;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FoodFeignClientCallBack implements FoodFeignClient {

    @Override
    public CommonResult<List> listFoodByBusinessId(Integer businessId) {
        //返回降级相应 , 食品信息返回null
        return new CommonResult(403,"feign触发了熔断降级",null);
    }
}
