package com.neusoft.mapper;

import com.neusoft.po.Remark;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RemarkMapper {

    @Select("select * from remarks where businessId=#{businessId}")
    public List<Remark> listRemarksByBusinessId(Integer businessId);
    
    @Insert("insert into remarks(remark,businessId,remarkDate,userId,userName) values(#{remark},#{businessId},#{remarkDate},#{userId},#{userName})")
    @Options(useGeneratedKeys = true, keyProperty = "remarkId", keyColumn = "remarkId")
    public int saveRemarks(Remark remark);
    
    //删除某用户某条评论
    @Select("delete from remarks where userId=#{userId} and businessId =#{businessId } and remark=#{remark}" )
    public int removeOneRemark(Remark remark);
    
    
    //删除某用户所有评论
    @Select("delete from remark where userId=#{userId}")
    public List<Remark> removeAllRemarksByUserId(String userId);

    @Select("select count(*) from remarks where userId=#{userId} and DATE(remarkDate) = CURDATE()")
    int getAmountByDayById(String userId);
}
