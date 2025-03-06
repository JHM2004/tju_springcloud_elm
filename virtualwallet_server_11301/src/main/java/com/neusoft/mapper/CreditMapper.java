package com.neusoft.mapper;

import com.neusoft.po.EfficientCredit;
import com.neusoft.po.EfficientCreditVo;
import com.neusoft.po.ExpiredCredit;
import com.neusoft.po.ExpiredCreditVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CreditMapper {

    /*
    * ---------------------------------对 efficient_credit 表的操作---------------------------------
    * */

    @Insert("insert into efficient_credit(user_id,channel_id,type_id,credit,create_time,expired_time,efficient) " +
            "values(#{userId},3,0,#{credit},NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR),0)")
    void addCreditByDateById(String userId,int credit);

    //1:当日已经签到 0：未签到
    @Select("SELECT COUNT(*) > 0 " +
            "FROM efficient_credit " +
            "WHERE user_id = #{userId} " +
            "AND DATE(create_time) = CURDATE() "+
            "AND channel_id =3")
    int signInOrNot(String userId);

    @Select("SELECT COUNT(*) > 0 " +
            "FROM efficient_credit " +
            "WHERE user_id = #{userId} " +
            "AND DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) " +
            "AND channel_id = 3")
    int signInOrNotYesterday(String userId);

    @Select("SELECT credit " +
            "FROM efficient_credit " +
            "WHERE user_id = #{userId} " +
            "AND DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) " +
            "AND channel_id = 3")
    int getCreditYesterdayByDateById(String userId);


    @Insert("insert into efficient_credit(user_id,channel_id,type_id,credit,create_time,expired_time,efficient) " +
            "values(#{userId},#{channelId},#{typeId},#{credit},NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR),0)")
    void addCreditRecharge(EfficientCredit efficientCredit);


    @Select("SELECT * FROM efficient_credit WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<EfficientCreditVo> listEfficientCreditById(@Param("userId") String userId);

    @Select("SELECT * FROM efficient_credit WHERE user_id = #{userId} and type_id=0 order by create_time desc")
    List<EfficientCreditVo> listEfficientGetCreditById(String userId);

    @Select("SELECT * FROM efficient_credit WHERE user_id = #{userId} and type_id=1 order by create_time desc")
    List<EfficientCreditVo> listCredit2MoneyById(String userId);

    @Select("SELECT * FROM efficient_credit WHERE type_id = 0 and expired_time<=NOW()")
    List<EfficientCredit> listExpiredCreditAtEfficientCredit();

    void removeByIDsBatch(@Param("list") List<Integer> list);



    /*
     * ---------------------------------对 credit 表的操作---------------------------------
     * */

    @Update("update credit set credit_all=#{credit}+credit_all where user_id=#{userId}")
    void addCreditById(String userId,int credit);

    @Select("SELECT credit_all FROM credit WHERE user_id = #{userId}")
    int getCreditAllById(String userId);

    @Select("SELECT count(*) FROM credit WHERE user_id = #{userId}")
    int existId(String userId);

    @Insert("insert into credit(user_id,credit_all) values(#{userId},0)")
    void addIdInfo(String userId);

    @Update("update credit set credit_all=credit_all-#{credit} where user_id=#{userId}")
    void decreaseCreditById(EfficientCredit efficientCredit);


    /*
     * ---------------------------------对 expired_credit 表的操作---------------------------------
     * */

    @Select("SELECT * FROM expired_credit WHERE user_id = #{userId} order by create_time desc")
    List<ExpiredCreditVo> listExpiredCreditById(String userId);


    void insertExpiredCreditBatch(List<ExpiredCredit> list);

}
