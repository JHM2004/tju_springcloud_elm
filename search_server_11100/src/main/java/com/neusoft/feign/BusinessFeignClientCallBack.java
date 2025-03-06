package com.neusoft.feign;

import com.neusoft.po.Business;
import com.neusoft.po.CommonResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class BusinessFeignClientCallBack implements BusinessFeignClient {

    @Override
    public CommonResult<List> listBusinessByBusinessName(@RequestBody Business business){
        //返回降级相应 , 食品信息返回null
        return new CommonResult(403,"feign触发了熔断降级",null);
    }
}