package com.neusoft.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neusoft.feign.BusinessFeignClient;
import com.neusoft.po.CommonResult;
import com.neusoft.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neusoft.po.Business;
import com.neusoft.po.History;

@RestController
@RequestMapping("/SearchController")
@RefreshScope
public class SearchController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private BusinessFeignClient businessFeignClient;

    @RequestMapping("/listBusiness")
    public CommonResult<List> listBusiness(@RequestBody History history) throws Exception {
        String userId = history.getUserId();
        String searchContent = history.getSearchContent();
        StringBuffer str = new StringBuffer();
        List<Business> businessList = new ArrayList<>();
        for (char ch : searchContent.toCharArray()) {
            str.append(ch);
            String nowContent = str.toString();
            Business business = new Business();
            business.setBusinessName(nowContent);
            businessList.addAll(businessFeignClient.listBusinessByBusinessName(business).getResult());
        }

        //去重：
        Set<Business> set = new HashSet<>();
        for (Business i : businessList) {
            set.add(i);
        }
        businessList.clear();
        businessList.addAll(set);

        Collections.reverse(businessList);


        //将本次的搜索语句存到historysearch数据库表中，注：本次的搜索语句是searchContent
        historyService.saveHistory(history);

        return new CommonResult(200, "success", businessList);
    }

    @RequestMapping("/getHistoryByUserId")
    public CommonResult<String> getHistoryByUserId(@RequestBody History history) throws Exception {
        String historyByUserId = historyService.getHistoryByUserId(history);
        return new CommonResult(200, "success", historyByUserId);
    }
}
