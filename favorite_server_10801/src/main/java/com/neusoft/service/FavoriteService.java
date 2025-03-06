package com.neusoft.service;

import com.neusoft.po.Favorite;

import java.util.List;

public interface FavoriteService {
	public int saveFavoriteBusinessId(Favorite favorite);
	public List<Integer> listFavoriteByUserId(Favorite favorite);
	public int removeFavoriteBusinessId(Favorite favorite);
}
