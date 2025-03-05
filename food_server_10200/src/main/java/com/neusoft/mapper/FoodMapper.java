package com.neusoft.mapper;
import com.neusoft.po.Food;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FoodMapper {

 @Select("select * from food where businessId=#{businessId} order by foodId")
 public List<Food> listFoodByBusinessId(Integer businessId);
 
 @Select("select * from food where foodId=#{foodId}")
 public Food getFoodById(Integer foodId);
 
 @Insert("insert into food(foodName,foodExplain,foodImg,foodPrice,businessId) values(#{foodName},#{foodExplain},#{foodImg},#{foodPrice},#{businessId})")
 public int addFood(Food food);
 
}