package com.neusoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neusoft.po.Business;
import com.neusoft.po.Remark;
import com.neusoft.service.BusinessService;
import com.neusoft.service.RemarkService;

@RestController
@RequestMapping("/RemarkController")
@RefreshScope
public class RemarkController {
    @Autowired
    private RemarkService remarkService;
    
    //需要businessId
    @RequestMapping("/listRemarksByBussinessId")
    public List<Remark> listRemarksByBussinessId(@RequestBody Remark remark){
        return remarkService.listRemarksByBusinessId(remark.getBusinessId());
    }
    
    //需要userId,businessId,userName,remark
    @RequestMapping("/saveRemarks")
    public int saveRemarks(@RequestBody Remark remark) {
		return remarkService.saveRemarks(remark);
	}
    
    @RequestMapping("/removeOneRemark")
    public int removeOneRemark(@RequestBody Remark remark){
    	return remarkService.removeOneRemark(remark);
    }

    //需要userId
    @RequestMapping("/removeAllRemarksByUserId")
    public List<Remark> removeAllRemarksByUserId(String userId){
    	return remarkService.removeAllRemarksByUserId(userId);
    }
    
    
}
