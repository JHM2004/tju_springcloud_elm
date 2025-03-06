package com.neusoft.service;

import com.neusoft.po.User;
import com.neusoft.po.UserAvatar;
import com.neusoft.po.UserPsd;

public interface UserService {
	
	public User getUserByIdByPass(User user);
 
	public User getUserById(String userId);
 
	public int saveUser(User user);


    public int changeUserPassword(UserPsd userPsd);

    public int changeUserAvatar(UserAvatar userAvatar);
    
    public int changeUserName(User user);
    
    public int userIdExists(User user);
}

