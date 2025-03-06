package com.neusoft.controller;

import com.neusoft.po.CommonResult;
import com.neusoft.po.User;
import com.neusoft.po.UserAvatar;
import com.neusoft.po.UserPsd;
import com.neusoft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/UserController")
@RefreshScope
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserByIdByPass/{userId}/{password}")
    public CommonResult<User> getUserByIdByPass(
            @PathVariable("userId") String userId,
            @PathVariable("password") String password
    ) throws Exception {
        User param = new User();
        param.setUserId(userId);
        param.setPassword(password);
        User userByIdByPass = userService.getUserByIdByPass(param);
        return new CommonResult(200, "success", userByIdByPass);
    }

    @GetMapping("/getUserById/{userId}")
    public CommonResult<Integer> getUserById(
            @PathVariable("userId") String userId
    ) throws Exception {
        User userById = userService.getUserById(userId);
        return new CommonResult(200, "success", userById);
    }

    @PostMapping("/saveUser/{userId}/{password}/{userName}/{userSex}")
    public CommonResult<Integer> saveUser(
            @PathVariable("userId") String userId,
            @PathVariable("password") String password,
            @PathVariable("userName") String userName,
            @PathVariable("userSex") Integer userSex
    ) throws Exception {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        user.setUserName(userName);
        user.setUserSex(userSex);
        int result = userService.saveUser(user);
        return new CommonResult(200, "success", result);
    }


// TODO 下面也应该修改为restful风格

    @RequestMapping("/changeUserAvatar")
    public CommonResult<Integer> changeUserAvatar(@RequestBody UserAvatar userAvatar) throws Exception {
        int i = userService.changeUserAvatar(userAvatar);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/changeUserName")
    public CommonResult<Integer> changeUserName(@RequestBody User user) throws Exception {
        int i = userService.changeUserName(user);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/changeUserPassword")
    public CommonResult<Integer> changeUserPassword(@RequestBody UserPsd userPsd) throws Exception {
        int i = userService.changeUserPassword(userPsd);
        return new CommonResult(200, "success", i);
    }

    @RequestMapping("/userIdExists")
    public CommonResult<Integer> userIdExists(@RequestBody User user) throws Exception {
        int i = userService.userIdExists(user);
        return new CommonResult(200, "success", i);
    }

}