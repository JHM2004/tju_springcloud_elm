package com.neusoft.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.neusoft.po.Likes;
import com.neusoft.po.User;

@Mapper
public interface LikesMapper {
	 @Select("select count(*) from likes where businessId=#{businessId}")
	 public int getLikesBybusinessId(Likes likes);
	 
	 @Insert("insert into likes values(#{userId},#{businessId})")
	 public int saveLikes(Likes likes);
	 
	 @Delete("delete from likes where userId=#{userId} and businessId=#{businessId}")
	 public int removeLikes(Likes likes);
	 
	 @Select("select count(*) from likes where userId=#{userId} and businessId=#{businessId}")
	 public int getLikesByUserIdByBusinessId(Likes likes);
}
