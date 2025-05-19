package com.sean.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sean.seckill.common.BaseResponse;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.request.LoginRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sean
 * @Date 2025/31/18
 */
public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param loginRequest 登录请求封装
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return BaseResponse
     */
    BaseResponse UserLogin(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户信息
     * @param userTicket session的key，根据 key 到 session 中获取对象信息
     * @param request request
     * @param response response
     * @return User
     */
    User getUserByCookie(String userTicket,HttpServletRequest request, HttpServletResponse response);

    /**
     * 修改用户密码，让redis和mysql中的数据保存一致
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    BaseResponse updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);

}
