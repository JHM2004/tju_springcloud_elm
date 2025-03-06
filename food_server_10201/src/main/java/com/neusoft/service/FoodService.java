package com.neusoft.service;

import com.neusoft.po.Food;

import java.util.List;

public interface FoodService {
	
 public List<Food> listFoodByBusinessId(Integer businessId);
 
 public int addFood(Food food);
}