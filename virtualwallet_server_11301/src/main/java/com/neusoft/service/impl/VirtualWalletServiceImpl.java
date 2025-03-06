package com.neusoft.service.impl;

import com.neusoft.mapper.CreditMapper;
import com.neusoft.mapper.VirtualWalletMapper;
import com.neusoft.po.MessageMoneyDto;
import com.neusoft.po.UserWallet;
import com.neusoft.po.WalletFlowDto;
import com.neusoft.po.WalletFlowVo;
import com.neusoft.service.VirtualWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VirtualWalletServiceImpl implements VirtualWalletService {

    @Autowired
    private VirtualWalletMapper virtualWalletMapper;

    @Autowired
    private CreditMapper creditMapper;

    @Override
    public int addWalletById(String userId) {
        return virtualWalletMapper.addWalletById(userId);
    }

    @Override
    public int getWhetherEnabledById(String userId) {
        return virtualWalletMapper.getWhetherEnabledById(userId);
    }

    @Override
    @Transactional
    public int rechargeById(WalletFlowDto walletFlowDto) {
        //充值时可以满 1000 元赠 100 元
        BigDecimal amount_old = walletFlowDto.getAmount();
        BigDecimal amount = walletFlowDto.getAmount();
        virtualWalletMapper.addRechargeFlow(walletFlowDto);
        int result  = virtualWalletMapper.rechargeById(walletFlowDto);
//        {
//            // 最终可以将下面的充值增积分功能设置为异步通信调用 (已经实现！！！)---------------------------
//            // 现在暂时是同步调用的代码
//            //用户可以获得i积分
//            int i = amount.setScale(0, RoundingMode.DOWN).intValue();
//            i = i / 100 * 10;
//            EfficientCredit efficientCredit = new EfficientCredit();
//            efficientCredit.setUserId(walletFlowDto.getUserId());
//            efficientCredit.setChannelId(0);
//            efficientCredit.setTypeId(0);
//            efficientCredit.setCredit(i);
//            creditMapper.addCreditRecharge(efficientCredit);
//            creditMapper.addCreditById(efficientCredit.getUserId(), i);
//            //----------------------------------------------------------------------------------
//        }
        MessageMoneyDto messageMoneyDto = new MessageMoneyDto();
        return result ;
    }

    @Override
    @Transactional
    public BigDecimal withdrawById(WalletFlowDto walletFlowDto) {
        if(virtualWalletMapper.getAmountById(walletFlowDto).compareTo(walletFlowDto.getAmount()) < 0){
            return BigDecimal.valueOf(0);
        }
        virtualWalletMapper.addWithdrawFlow(walletFlowDto);
        virtualWalletMapper.withdrawById(walletFlowDto);
        BigDecimal amount = walletFlowDto.getAmount();

        //TODO 逻辑有没有问题
        return amount;
    }

    @Override
    @Transactional
    public int pay(WalletFlowDto walletFlowDto) {
        //转账人钱不够
        if(virtualWalletMapper.getAmountById(walletFlowDto).compareTo(walletFlowDto.getAmount()) < 0){
            return 0;
        }
        //不能转账给自己
        if(walletFlowDto.getUserId()==walletFlowDto.getTargetId()){
            return 0;
        }
        //支付对方不存在的问题
        if(virtualWalletMapper.getCountById(walletFlowDto.getTargetId()) ==0 ){
            return 0;
        }
        //这里如果发生异常，则事务回滚+在controller层中return 0
        virtualWalletMapper.addPayFlow(walletFlowDto);
        virtualWalletMapper.withdrawById(walletFlowDto);
        WalletFlowDto walletFlowDto1 = new WalletFlowDto();
        walletFlowDto1.setAmount(walletFlowDto.getAmount());
        walletFlowDto1.setUserId(walletFlowDto.getTargetId());
        virtualWalletMapper.rechargeById(walletFlowDto1);
//        {
//            // 最终可以将下面的支付增积分功能设置为异步通信调用(已经完成！！)--------------------------------------
//            // 现在暂时是同步调用的代码
//            //用户可以获得i积分
//            int i = walletFlowDto.getAmount().setScale(0, RoundingMode.DOWN).intValue();
//            i = i / 10 * 3;
//            EfficientCredit efficientCredit = new EfficientCredit();
//            efficientCredit.setUserId(walletFlowDto.getUserId());
//            efficientCredit.setChannelId(1);
//            efficientCredit.setTypeId(0);
//            efficientCredit.setCredit(i);
//            creditMapper.addCreditRecharge(efficientCredit);
//            creditMapper.addCreditById(efficientCredit.getUserId(), i);
//            //------------------------------------------------------------------------------------
//        }
        MessageMoneyDto messageMoneyDto = new MessageMoneyDto();

        return 1;
    }

    @Override
    @Transactional
    public BigDecimal getBalanceById(String userId) {
        WalletFlowDto walletFlowDto = new WalletFlowDto();
        walletFlowDto.setUserId(userId);
        return virtualWalletMapper.getAmountById(walletFlowDto);
    }

    @Override
    public List<WalletFlowVo> listWalletLogById(String userId) {
        return virtualWalletMapper.listWalletLogById(userId);
    }

    @Override
    public List<WalletFlowVo> listWalletLogIncomeById(String userId) {
        return virtualWalletMapper.listWalletLogIncomeById(userId);
    }

    @Override
    public List<WalletFlowVo> listWalletLogOutcomeById(String userId) {
        return virtualWalletMapper.listWalletLogOutcomeById(userId);
    }

    @Override
    public int getFreeze(String userId) {
        return virtualWalletMapper.getFreeze(userId);
    }

    @Override
    public int freeze(String userId) {
        return virtualWalletMapper.freeze(userId);
    }


    @Override
    @Transactional
    public int unFreeze(String userId) {
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(userId);
        userWallet.unFreeze();
        return virtualWalletMapper.unFreeze(userWallet);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void rechargeByIdByCredit(String userId, int i) {
        virtualWalletMapper.rechargeByIdByCredit(userId,i);
        virtualWalletMapper.addWithdrawFlowByRechargeByIdByCredit(userId,i);
    }

}
