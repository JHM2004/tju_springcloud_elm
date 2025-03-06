package com.neusoft.mapper;

import com.neusoft.po.Chats;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatsMapper {
	 @Select("select * from chats where currentUserId =#{currentUserId}")
	 public List<Chats> getChatsByUserId(String currentUserId);

	 @Insert("insert into chats values(#{currentUserId},#{senderUserId},#{receiverUserId},#{message})")
	 public int saveChats(Chats chats);
	 
	 @Delete("delete from chats where currentUserId=#{currentUserId}")
	 public int removeChatsAllByCurrentUserId(Chats chats);
	 
	 @Delete("delete from chats where currentUserId=#{currentUserId} and senderUserId=#{senderUserId} and "
	 		+ "receiverUserId=#{receiverUserId} and message=#{message}")
	 public int removeChatsByTwoUserIdByMessage(Chats chats);
	 
	 @Delete("delete from chats where currentUserId=#{currentUserId} and receiverUserId=#{receiverUserId} and "
	 		+ "message=#{message}")
	 public int recallChatsByTwoUserIdByMessage(Chats chats);
}
