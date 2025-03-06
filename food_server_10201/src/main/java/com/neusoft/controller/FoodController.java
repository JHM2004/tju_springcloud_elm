package com.neusoft.controller;

import com.neusoft.po.CommonResult;
import com.neusoft.po.Food;
import com.neusoft.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/FoodController")
@RefreshScope
public class FoodController {

    @Autowired
    private FoodService foodService;


    @GetMapping("/listFoodByBusinessId/{businessId}")
    public CommonResult<List> listFoodByBusinessId(
            @PathVariable("businessId") Integer businessId
    ) throws Exception {
        List<Food> list = foodService.listFoodByBusinessId(businessId);
        return new CommonResult(200, "success(10201)", list);
    }

    // TODO 下面也可以改成restful风格的api
    @RequestMapping("/addFood")
    public CommonResult<Integer> addFood(@RequestBody Food food) throws Exception {
        int i =  foodService.addFood(food);
        return new CommonResult(200, "success(200)", i);
    }

}