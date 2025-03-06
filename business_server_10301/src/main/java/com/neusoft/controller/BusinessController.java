package com.neusoft.controller;

import com.neusoft.feign.FoodFeignClient;
import com.neusoft.po.Business;
import com.neusoft.po.CommonResult;
import com.neusoft.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/BusinessController")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private FoodFeignClient foodFeignClient;

    @GetMapping("/listBusinessByOrderTypeId/{orderTypeId}")
    public CommonResult<List> listBusinessByOrderTypeId(
            @PathVariable("orderTypeId") Integer orderTypeId
    ) throws Exception {
        List<Business> list = businessService.listBusinessByOrderTypeId(orderTypeId);
        return new CommonResult(200, "success", list);
    }

    @GetMapping("/getBusinessById/{businessId}")
    public CommonResult<Business> getBusinessById(
            @PathVariable("businessId") Integer businessId
    ) throws Exception {
        Business business = businessService.getBusinessById(businessId);
        //在商家微服务中调用食品微服务
        CommonResult<List> result = foodFeignClient.listFoodByBusinessId(businessId);
        // 如果食品微服务返回降级相应,那么就返回空集合
        if (result.getCode() == 200) {
            business.setFoodList(result.getResult());
        }else{
            business.setFoodList(new ArrayList());
        }
        return new CommonResult(200, "success(10301)", business);
    }

    // TODO 下面也可以改成restful风格的api
    @RequestMapping("/saveBusiness")
    public CommonResult<Integer> saveBusiness(@RequestBody Business business) throws Exception {
        int i = businessService.saveBusiness(business);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/getBusinessIdByPhoneNumber")
    public CommonResult<Integer> getBusinessIdByPhoneNumber(@RequestBody Business business) throws Exception {
        int  i =  businessService.getBusinessIdByPhoneNumber(business);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/checkBusiness")
    public CommonResult<Integer> checkBusiness(@RequestBody Business business) throws Exception {
        int i  =  businessService.checkBusiness(business);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/getBusinessByIdByPass")
    public CommonResult<Business> getBusinessByIdByPass(@RequestBody Business business) {
        Business businessByIdByPass = businessService.getBusinessByIdByPass(business);
        return new CommonResult(200, "success", businessByIdByPass);
    }

    @RequestMapping("/listBusinessByBusinessName")
    public CommonResult<List> listBusinessByBusinessName(@RequestBody Business business) throws Exception {
        List<Business> businesses = businessService.listBusinessByBusinessName(business.getBusinessName());
        return new CommonResult(200, "success", businesses);
    }

    @RequestMapping("/updateBusiness")
    public CommonResult<Integer> updateBusiness(@RequestBody Business business) throws Exception {
        int i =  businessService.updateBusiness(business);
        return new CommonResult(200, "success", i);
    }
}