package com.neusoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.mapper.LikesMapper;
import com.neusoft.po.Likes;
import com.neusoft.service.LikesService;

@Service
public class LikesServiceImpl implements LikesService {
	
	 @Autowired
	 private LikesMapper likesMapper;
	
	 @Override
	 public int getLikesBybusinessId(Likes likes) {
		 return likesMapper.getLikesBybusinessId(likes);
	 }
	 
	 @Override
	 public int saveLikes(Likes likes) {
		 return likesMapper.saveLikes(likes);
	 }
	 
	 @Override
	 public int removeLikes(Likes likes) {
		 return likesMapper.removeLikes(likes);
	 }
	 
	 @Override
	 public int getLikesByUserIdByBusinessId(Likes likes) {
		 return likesMapper.getLikesByUserIdByBusinessId(likes);
	 }
}