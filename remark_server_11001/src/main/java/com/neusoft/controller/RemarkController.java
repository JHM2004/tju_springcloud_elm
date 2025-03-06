package com.neusoft.controller;

import java.util.List;

import com.neusoft.po.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neusoft.po.Remark;
import com.neusoft.service.RemarkService;

@RestController
@RequestMapping("/RemarkController")
@RefreshScope
public class RemarkController {
    @Autowired
    private RemarkService remarkService;

    //需要businessId
    @RequestMapping("/listRemarksByBussinessId")
    public CommonResult<List> listRemarksByBussinessId(@RequestBody Remark remark) {
        List<Remark> remarks = remarkService.listRemarksByBusinessId(remark.getBusinessId());
        return new CommonResult(200, "success", remarks);
    }

    //需要userId,businessId,userName,remark
    @RequestMapping("/saveRemarks")
    public CommonResult<Integer> saveRemarks(@RequestBody Remark remark) {
        int i = remarkService.saveRemarks(remark);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/removeOneRemark")
    public CommonResult<Integer> removeOneRemark(@RequestBody Remark remark) {
        int i = remarkService.removeOneRemark(remark);
        return new CommonResult(200, "success", i);
    }

    //需要userId
    @RequestMapping("/removeAllRemarksByUserId")
    public CommonResult<List> removeAllRemarksByUserId(String userId) {
        List<Remark> remarks = remarkService.removeAllRemarksByUserId(userId);
        return new CommonResult(200, "success", remarks);
    }


}
