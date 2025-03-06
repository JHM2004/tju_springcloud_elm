package com.neusoft.service;

import com.neusoft.po.History;

public interface HistoryService {
	public String getHistoryByUserId(History history);
	public int saveHistory(History history);
}
