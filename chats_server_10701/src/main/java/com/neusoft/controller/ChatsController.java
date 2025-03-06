package com.neusoft.controller;

import com.neusoft.po.Chats;
import com.neusoft.po.CommonResult;
import com.neusoft.service.ChatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ChatsController")
@RefreshScope
public class ChatsController {
	@Autowired 
	private ChatsService chatsService;

	// TODO  修改成restful风格的api

	@RequestMapping("/getChatsByUserId")
	public CommonResult<List> getChatsByUserId(@RequestBody Chats chats){
		List<Chats> list =  chatsService.getChatsByUserId(chats.getCurrentUserId());
		return new CommonResult(200,"success",list);
	}
	
	@RequestMapping("/saveChats")
	public CommonResult<Integer> saveChats(@RequestBody Chats chats) {
		int i = chatsService.saveChats(chats);
		return new CommonResult(200,"success",i);
	}
	
	@RequestMapping("/removeChatsAllByCurrentUserId")
	public CommonResult<Integer> removeChatsAllByCurrentUserId(@RequestBody Chats chats) {
		int i =  chatsService.removeChatsAllByCurrentUserId(chats);
		return new CommonResult(200,"success",i);
	}
	
	@RequestMapping("/removeChatsByTwoUserIdByMessage")
	public CommonResult<Integer> removeChatsByTwoUserIdByMessage(@RequestBody Chats chats) {
		int i = chatsService.removeChatsByTwoUserIdByMessage(chats);
		return new CommonResult(200,"success",i);
	}
	
	@RequestMapping("/recallChatsByTwoUserIdByMessage")
	public CommonResult<Integer> recallChatsByTwoUserIdByMessage(@RequestBody Chats chats) {
		int i =  chatsService.recallChatsByTwoUserIdByMessage(chats);
		return new CommonResult(200,"success",i);
	}
}
