package com.neusoft.controller;

import com.neusoft.po.Business;
import com.neusoft.po.CommonResult;
import com.neusoft.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;

@RestController
@RequestMapping("/BusinessController")
@CrossOrigin("*")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ResourceUrlProvider mvcResourceUrlProvider;


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
        CommonResult<List> result = restTemplate.getForObject("http://localhost:10200/FoodController/listFoodByBusinessId/" + business, CommonResult.class);
        if (result.getCode() == 200) {
            business.setFoodList(result.getResult());
        }
        return new CommonResult(200, "success", business);
    }


    // TODO 下面也可以改成restful风格的api
    @RequestMapping("/saveBusiness")
    public int saveBusiness(@RequestBody Business business) throws Exception {
        return businessService.saveBusiness(business);
    }

    @RequestMapping("/getBusinessIdByPhoneNumber")
    public int getBusinessIdByPhoneNumber(@RequestBody Business business) throws Exception {
        return businessService.getBusinessIdByPhoneNumber(business);
    }

    @RequestMapping("/checkBusiness")
    public int checkBusiness(@RequestBody Business business) throws Exception {
        return businessService.checkBusiness(business);
    }


    @RequestMapping("/getBusinessByIdByPass")
    public Business getBusinessByIdByPass(@RequestBody Business business) {
        return businessService.getBusinessByIdByPass(business);
    }

    @RequestMapping("/listBusinessByBusinessName")
    public List<Business> listBusinessByBusinessName(@RequestBody Business business) throws Exception {
        return businessService.listBusinessByBusinessName(business.getBusinessName());
    }

    @RequestMapping("/updateBusiness")
    public int updateBusiness(@RequestBody Business business) throws Exception {
        return businessService.updateBusiness(business);
    }

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}