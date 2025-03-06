package com.neusoft.service.impl;

import com.neusoft.mapper.CreditMapper;
import com.neusoft.mapper.RemarkMapper;
import com.neusoft.po.Remark;
import com.neusoft.service.RemarkService;
import com.neusoft.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RemarkServiceImpl implements RemarkService{

	 @Autowired
	 private RemarkMapper remarkMapper;

	@Autowired
	private CreditMapper creditMapper;


	@Override
	public List<Remark> listRemarksByBusinessId(Integer businessId) {
		
		return remarkMapper.listRemarksByBusinessId(businessId);
	}

	@Override
	@Transactional
	@CachePut(cacheNames = "getCreditById", key = "#remark.getUserId()")
	public int saveRemarks(Remark remark) {
		//核心业务
		remark.setRemarkDate(CommonUtil.getCurrentDate());
//		/*------------------------------------------------------------------------------------
//		 * 评论一条获得1积分，每天评论超过10条后取消积分奖励，即：每天通过评论最多获得10积分封顶
//		 * */
//		//获取当日已经评论的条数
//		// 可以将下面的评论加积分的功能做成异步通信调用，提升性能（已经完成）
//		int count = remarkMapper.getAmountByDayById(remark.getUserId());
//		if(count<10){
//			//积分+1
//			EfficientCredit efficientCredit = new EfficientCredit();
//			efficientCredit.setUserId(remark.getUserId());
//			efficientCredit.setChannelId(2);
//			efficientCredit.setTypeId(0);
//			efficientCredit.setCredit(1);
//			creditMapper.addCreditRecharge(efficientCredit);
//			creditMapper.addCreditById(efficientCredit.getUserId(), 1);
//		}
//		//------------------------------------------------------------------------------------
		//返回值是评论编号
		remarkMapper.saveRemarks(remark);
		return creditMapper.getCreditAllById(remark.getUserId());
	}
	
	@Override
	public int removeOneRemark(Remark remark) {
		return remarkMapper.removeOneRemark(remark);
	}
	
	
	@Override
	public List<Remark> removeAllRemarksByUserId(String userId) {
		return remarkMapper.removeAllRemarksByUserId(userId);
	}
	
}
