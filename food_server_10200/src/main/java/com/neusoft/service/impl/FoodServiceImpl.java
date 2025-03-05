package com.neusoft.service.impl;
import com.neusoft.mapper.FoodMapper;
import com.neusoft.po.Food;
import com.neusoft.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
 
 @Autowired
 private FoodMapper foodMapper;
 
 @Override
 public List<Food> listFoodByBusinessId(Integer businessId) {
 return foodMapper.listFoodByBusinessId(businessId);
 }
 
 @Override
 public int addFood(Food food) {
	 return foodMapper.addFood(food);
 }
}