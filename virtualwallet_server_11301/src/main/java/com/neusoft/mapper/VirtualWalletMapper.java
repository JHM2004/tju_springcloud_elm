package com.neusoft.mapper;

import com.neusoft.po.UserWallet;
import com.neusoft.po.WalletFlowDto;
import com.neusoft.po.WalletFlowVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface VirtualWalletMapper {

    @Insert("insert into user_wallet(user_id,balance,freeze) values(#{userId},0,0)")
    int addWalletById(String userId);

    @Select("select count(*) from user_wallet where user_id =#{userId}")
    int getWhetherEnabledById(String userId);

    @Update("update user_wallet set balance=#{amount}+balance where user_id=#{userId}")
    int rechargeById(WalletFlowDto walletFlowDto);

    @Insert("insert into wallet_log(user_id,target_type,fee) values(#{userId},1,#{amount})")
    void addRechargeFlow(WalletFlowDto walletFlowDto);

    @Update("update user_wallet set balance=balance-#{amount} where user_id=#{userId}")
    int withdrawById(WalletFlowDto walletFlowDto);

    @Insert("insert into wallet_log(user_id,target_type,fee) values(#{userId},2,#{amount})")
    void addWithdrawFlow(WalletFlowDto walletFlowDto);

    @Select("select balance from user_wallet where user_id =#{userId}")
    BigDecimal getAmountById(WalletFlowDto walletFlowDto);

    @Insert("insert into wallet_log(user_id,target_type,target_id,fee) values(#{userId},3,#{targetId},#{amount})")
    void addPayFlow(WalletFlowDto walletFlowDto);

    @Select("select * from wallet_log where user_id =#{userId} or target_id=#{userId}")
    List<WalletFlowVo> listWalletLogById(String userId);

    @Select("select * from wallet_log where (user_id =#{userId} and target_type=1) or (target_type=3 and target_id=#{userId})")
    List<WalletFlowVo> listWalletLogIncomeById(String userId);

    @Select("select * from wallet_log where (user_id =#{userId} and target_type=2) or (target_type=3 and user_id=#{userId})")
    List<WalletFlowVo> listWalletLogOutcomeById(String userId);

    @Select("select freeze from user_wallet where user_id =#{userId}")
    int getFreeze(String userId);

    @Update("update user_wallet set freeze=1 where user_id=#{userId}")
    int freeze(String userId);

    @Update("update user_wallet set freeze=0 where user_id=#{userId}")
    int unFreeze(UserWallet userWallet);

    @Select("select count(*) from user_wallet where (user_id =#{userId})")
    int getCountById(String userId);

    @Update("update user_wallet set balance=#{i}+balance where user_id=#{userId}")
    void rechargeByIdByCredit(String userId, int i);

    @Insert("insert into wallet_log(user_id,target_type,fee) values(#{userId},1,#{i})")
    void addWithdrawFlowByRechargeByIdByCredit(String userId, int i);
}

