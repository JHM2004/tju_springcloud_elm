package com.neusoft.service;

import com.neusoft.po.Chats;

import java.util.List;

public interface ChatsService {
	
	 public List<Chats> getChatsByUserId(String currentUserId);
	 public int saveChats(Chats chats);
	 public int removeChatsAllByCurrentUserId(Chats chats);
	 public int removeChatsByTwoUserIdByMessage(Chats chats);
	 public int recallChatsByTwoUserIdByMessage(Chats chats);
}
