package com.neusoft.service;

import java.util.List;

import com.neusoft.po.Business;
import com.neusoft.po.Remark;

public interface RemarkService {
	
	 public List<Remark> listRemarksByBusinessId(Integer businessId);
	 public int saveRemarks(Remark remark);
	 public int removeOneRemark(Remark remark);
	 
	 
	 
	 public List<Remark> removeAllRemarksByUserId(String userId);
	 
}
