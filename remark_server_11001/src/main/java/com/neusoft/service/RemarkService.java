package com.neusoft.service;

import com.neusoft.po.Remark;

import java.util.List;

public interface RemarkService {
	
	 public List<Remark> listRemarksByBusinessId(Integer businessId);
	 public int saveRemarks(Remark remark);
	 public int removeOneRemark(Remark remark);
	 public List<Remark> removeAllRemarksByUserId(String userId);
	 
}
