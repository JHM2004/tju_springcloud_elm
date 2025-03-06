package com.neusoft.controller;

import java.util.List;

import com.neusoft.po.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neusoft.po.Favorite;
import com.neusoft.service.FavoriteService;


@RestController
@RequestMapping("/FavoriteController")
@CrossOrigin
public class FavoriteController {
	
	@Autowired
	private FavoriteService favoriteService;

	//TODO 下面的代码可以改成restful风格的
	@RequestMapping("/saveFavoriteBusinessId")
	public CommonResult<Integer> saveFavoriteBusinessId(@RequestBody Favorite favorite) {
		int i =  favoriteService.saveFavoriteBusinessId(favorite);
		return new CommonResult(200,"success",i);
	}
	
	@RequestMapping("/listFavoriteByUserId")
	public CommonResult<List> listFavoriteByUserId(@RequestBody Favorite favorite) {
		List<Integer> integers = favoriteService.listFavoriteByUserId(favorite);
		return new CommonResult(200,"success",integers);
	}
	
	@RequestMapping("/removeFavoriteBusinessId")
	public CommonResult<Integer> removeFavoriteBusinessId(@RequestBody Favorite favorite) {
		int i  =  favoriteService.removeFavoriteBusinessId(favorite);
		return new CommonResult(200,"success",i);
	}
}
