package com.neusoft.controller;

import com.neusoft.po.CommonResult;
import com.neusoft.po.Likes;
import com.neusoft.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/LikesController")
@RefreshScope
public class LikesController {
	
	 @Autowired
	 private LikesService likesService;
	
	 @RequestMapping("/getLikesBybusinessId")
	 public CommonResult<Integer> getLikesBybusinessId(@RequestBody Likes likes) {
		 int i  = likesService.getLikesBybusinessId(likes);
		 return new CommonResult(200,"success",i);
	 }
	 
	 @RequestMapping("/saveLikes")
	 public CommonResult<Integer> saveLikes(@RequestBody Likes likes) {
		 int i =  likesService.saveLikes(likes);
		 return new CommonResult(200,"success",i);
	 }
	 
	 @RequestMapping("/removeLikes")
	 public CommonResult<Integer> removeLikes(@RequestBody Likes likes) {
		 int i =  likesService.removeLikes(likes);
		 return new CommonResult(200,"success",i);
	 }
	 
	 @RequestMapping("/getLikesByUserIdByBusinessId")
	 public CommonResult<Integer> getLikesByUserIdByBusinessId(@RequestBody Likes likes) {
		 int i =  likesService.getLikesByUserIdByBusinessId(likes);
		 return new CommonResult(200,"success",i);
	 }
}
