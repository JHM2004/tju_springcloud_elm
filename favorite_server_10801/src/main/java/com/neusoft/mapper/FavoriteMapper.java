
package com.neusoft.mapper;

import com.neusoft.po.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {
	
	 @Insert("insert into favorite values(#{userId},#{businessId})")
	 public int saveFavoriteBusinessId(Favorite favorite);
	
	 @Select("select businessId from favorite where userId=#{userId}")
	 public List<Integer> listFavoriteByUserId(Favorite favorite);

	 @Delete("delete from favorite where userId=#{userId} and businessId=#{businessId}")
	 public int removeFavoriteBusinessId(Favorite favorite);
}