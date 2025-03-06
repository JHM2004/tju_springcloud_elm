package com.neusoft.service;

import com.neusoft.po.Likes;

public interface LikesService {
	 public int getLikesBybusinessId(Likes likes);
	 public int saveLikes(Likes likes);
	 public int removeLikes(Likes likes);
	 public int getLikesByUserIdByBusinessId(Likes likes);
}
