package com.neusoft.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.mapper.RemarkMapper;
import com.neusoft.mapper.CreditMapper;
import com.neusoft.po.MessageMoneyDto;
import com.neusoft.po.EfficientCredit;
import com.neusoft.po.ExpiredCredit;
import com.neusoft.po.EfficientCreditVo;
import com.neusoft.po.ExpiredCreditVo;
import com.neusoft.service.CreditService;
import com.neusoft.service.VirtualWalletService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private VirtualWalletService virtualWalletService;

    @Autowired
    private RemarkMapper remarkMapper;

    @Override
    @Transactional
    public int addCreditByDateById(String userId) {
        //查找用户当天有没有签到
        AtomicInteger result = new AtomicInteger(creditMapper.signInOrNot(userId));
        //若签到了，返回0
        if(result.get() == 1){
            return 0;
        }
        //如果没签到，则签到，返回1
        int creditAddAmount = 0;
            //查找上一天的签到情况
        int i = creditMapper.signInOrNotYesterday(userId);
        //上一天没签到
        if(i==0) {
            creditAddAmount = 1;
        }else{
            //获取上一天签到获得的credit
            int beforeAmount =  creditMapper.getCreditYesterdayByDateById(userId);
            creditAddAmount = (beforeAmount==7?1:beforeAmount+1);
        }
        //签到并增加积分
        creditMapper.addCreditByDateById(userId,creditAddAmount);
        //增加用户积分总表
        creditMapper.addCreditById(userId,creditAddAmount);
        return creditMapper.getCreditAllById(userId);
    }

    @Override
    @Transactional
    public int credit2money(String userId, Integer credit) {
        EfficientCredit efficientCredit = new EfficientCredit();
        efficientCredit.setUserId(userId);
        efficientCredit.setChannelId(4);
        efficientCredit.setTypeId(1);
        efficientCredit.setCredit(credit*(-1));
        creditMapper.addCreditRecharge(efficientCredit);
        creditMapper.addCreditById(userId, credit*(-1));
        virtualWalletService.rechargeByIdByCredit(userId,credit/100);
        return creditMapper.getCreditAllById(userId);
    }


    @Override
    @Transactional
    public int getCreditById(String userId) {
        int i = creditMapper.existId(userId);
        if(i==1){
            return creditMapper.getCreditAllById(userId);
        }
        creditMapper.addIdInfo(userId);
        return 0;
    }

    @Override
    public List<EfficientCreditVo> listEfficientCreditById(String userId , Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);//开启分页查询
        List<EfficientCreditVo> all = creditMapper.listEfficientCreditById(userId);
        //将查询的page对象（page对象继承了ArrayList类）封装到PageInfo下就可以获取分页的各种数据
        PageInfo<EfficientCreditVo> pageInfo = new PageInfo<>(all);
//        int pageNum = pageInfo.getPageNum();//获取当前页
//        int pages = pageInfo.getPages();//获取总页数
//        int pageSize1 = pageInfo.getPageSize();//获取每页大小
//        int size = pageInfo.getSize();//获取当前页的记录数
//        long total = pageInfo.getTotal();//获取总记录数
//        List<EfficientCreditVo> list = pageInfo.getList();//获取当前页的内容
        return pageInfo.getList();
    }

    @Override
    public List<ExpiredCreditVo> listExpiredCreditById(String userId , Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);//开启分页查询
        List<ExpiredCreditVo> all = creditMapper.listExpiredCreditById(userId);
        //将查询的page对象（page对象继承了ArrayList类）封装到PageInfo下就可以获取分页的各种数据
        PageInfo<ExpiredCreditVo> pageInfo = new PageInfo<>(all);
//        int pageNum = pageInfo.getPageNum();//获取当前页
//        int pages = pageInfo.getPages();//获取总页数
//        int pageSize1 = pageInfo.getPageSize();//获取每页大小
//        int size = pageInfo.getSize();//获取当前页的记录数
//        long total = pageInfo.getTotal();//获取总记录数
//        List<ExpiredCreditVo> list = new CopyOnWriteArrayList<>(pageInfo.getList()); // 获取当前页的内容
        return pageInfo.getList() ;
    }

    @Override
    public List<EfficientCreditVo> listEfficientGetCreditById(String userId , Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);//开启分页查询
        List<EfficientCreditVo> all = creditMapper.listEfficientGetCreditById(userId);
        //将查询的page对象（page对象继承了ArrayList类）封装到PageInfo下就可以获取分页的各种数据
        PageInfo<EfficientCreditVo> pageInfo = new PageInfo<>(all);
//        int pageNum = pageInfo.getPageNum();//获取当前页
//        int pages = pageInfo.getPages();//获取总页数
//        int pageSize1 = pageInfo.getPageSize();//获取每页大小
//        int size = pageInfo.getSize();//获取当前页的记录数
//        long total = pageInfo.getTotal();//获取总记录数
//        List<EfficientCreditVo> list = pageInfo.getList();//获取当前页的内容
        return pageInfo.getList();
    }

    @Override
    public List<EfficientCreditVo> listCredit2MoneyById(String userId , Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);//开启分页查询
        List<EfficientCreditVo> all = creditMapper.listCredit2MoneyById(userId);
        //将查询的page对象（page对象继承了ArrayList类）封装到PageInfo下就可以获取分页的各种数据
        PageInfo<EfficientCreditVo> pageInfo = new PageInfo<>(all);
//        int pageNum = pageInfo.getPageNum();//获取当前页
//        int pages = pageInfo.getPages();//获取总页数
//        int pageSize1 = pageInfo.getPageSize();//获取每页大小
//        int size = pageInfo.getSize();//获取当前页的记录数
//        long total = pageInfo.getTotal();//获取总记录数
//        List<EfficientCreditVo> list = pageInfo.getList();//获取当前页的内容
        return pageInfo.getList();
    }

//--------------------------------------------（RabbitMQ的消息处理器）-----------------------------------------------

    @Transactional
    public void money2creditByRabbitMQ(MessageMoneyDto messageMoneyDto) {
        //用户可以获得i积分
        int i = messageMoneyDto.getAmount().setScale(0, RoundingMode.DOWN).intValue();
        i = i / 100 * 10;
        EfficientCredit efficientCredit = new EfficientCredit();
        efficientCredit.setUserId(messageMoneyDto.getUserId());
        efficientCredit.setChannelId(0);
        efficientCredit.setTypeId(0);
        efficientCredit.setCredit(i);
        creditMapper.addCreditRecharge(efficientCredit);
        creditMapper.addCreditById(efficientCredit.getUserId(), i);
    }

    @Transactional
    public void pay2creditByRabbitMQ(MessageMoneyDto messageMoneyDto) {
        int i = messageMoneyDto.getAmount().setScale(0, RoundingMode.DOWN).intValue();
        i = i / 10 * 3;
        EfficientCredit efficientCredit = new EfficientCredit();
        efficientCredit.setUserId(messageMoneyDto.getUserId());
        efficientCredit.setChannelId(1);
        efficientCredit.setTypeId(0);
        efficientCredit.setCredit(i);
        creditMapper.addCreditRecharge(efficientCredit);
        creditMapper.addCreditById(efficientCredit.getUserId(), i);
    }

    @Transactional
    public void remark2creditByRabbitMQ(String userId) {
		int count = remarkMapper.getAmountByDayById(userId);
		if(count<10) {
            //积分+1
            EfficientCredit efficientCredit = new EfficientCredit();
            efficientCredit.setUserId(userId);
            efficientCredit.setChannelId(2);
            efficientCredit.setTypeId(0);
            efficientCredit.setCredit(1);
            creditMapper.addCreditRecharge(efficientCredit);
            creditMapper.addCreditById(efficientCredit.getUserId(), 1);
        }
    }

//--------------------------------------------（定时任务）-----------------------------------------------
    @Scheduled(cron = "0 0 0 * * ?")  // 每天凌晨0点执行
    @Transactional
    public void checkForExpiredCredit() {
        //查出来在有效积分表中所有的type_id为0的过期一年的积分，放在list集合中
        List<EfficientCredit> list= creditMapper.listExpiredCreditAtEfficientCredit();
        //如果有需要处理的过期积分(增加代码的健壮性)
        if (list != null && !list.isEmpty()) {
            //将查出来在有效积分表中所有的type_id为0的过期一年的积分从有效积分表中删除
            List<Integer> listId = new ArrayList<>();
            for(EfficientCredit efficientCredit : list) {
                listId.add(efficientCredit.getId());
            }
            creditMapper.removeByIDsBatch(listId);
            //将list集合中的数据放在过期的积分表中
            List<ExpiredCredit> listExpiredCredit = new ArrayList<>();
            for(EfficientCredit efficientCredit : list) {
                ExpiredCredit expiredCredit = new ExpiredCredit();
                BeanUtils.copyProperties(efficientCredit, expiredCredit);
                expiredCredit.setEfficient(1);
                listExpiredCredit.add(expiredCredit);
            }
            creditMapper.insertExpiredCreditBatch(listExpiredCredit);
            //将credit表中用户的总积分改变（有的用户会减少积分）
            for(EfficientCredit efficientCredit : list) {
                creditMapper.decreaseCreditById(efficientCredit);
            }
        }
    }
}
