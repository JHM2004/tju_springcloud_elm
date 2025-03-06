package com.neusoft.feign;

import com.neusoft.po.Business;
import com.neusoft.po.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name="business-server",fallback= BusinessFeignClientCallBack.class)
public interface BusinessFeignClient {

    @RequestMapping("/listBusinessByBusinessName")
    public CommonResult<List> listBusinessByBusinessName(@RequestBody Business business);
}
