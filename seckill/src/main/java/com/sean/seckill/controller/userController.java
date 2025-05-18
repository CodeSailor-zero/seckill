package com.sean.seckill.controller;

import com.sean.seckill.common.BaseResponse;
import com.sean.seckill.model.request.LoginRequest;
import com.sean.seckill.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author sean
 * @Date 2025/50/18
 */
@Controller
@RequestMapping("/user")
public class userController {
    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public BaseResponse doLogin(@Valid LoginRequest loginRequest,
                                HttpServletRequest request,
                                HttpServletResponse response){
        return userService.UserLogin(loginRequest,request,response);
    }
}
