package com.sean.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.seckill.common.BaseResponse;
import com.sean.seckill.common.ErrorCodeEnum;
import com.sean.seckill.exception.GlobalException;
import com.sean.seckill.mapper.UserMapper;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.request.LoginRequest;
import com.sean.seckill.service.UserService;
import com.sean.seckill.util.CookieUtil;
import com.sean.seckill.util.MD5Util;
import com.sean.seckill.util.UUIDUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sean
 * @Date 2025/34/18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求封装
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @return BaseResponse
     */
    @Override
    public BaseResponse UserLogin(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        //取出参数，mobile和password
        String mobile = loginRequest.getMobile();
        String password = loginRequest.getPassword();

        // 通过 validation 库 校验参数是否为null，通过IsMobile注解校验手机号码是否合规
//        //校验参数，mobile和password
//        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password) ) {
//            return BaseResponse.error(ErrorCodeEnum.LOGIN_ERROR);
//        }
//        //校验手机号码是否合规
//        if (!VaildUtil.isMobile(mobile)) {
//            return BaseResponse.error(ErrorCodeEnum.MOBILE_ERROR);
//        }
        //查询数据库，用户是否存在
        User user = userMapper.selectById(mobile);
        if (user == null) {
            System.out.println("用户不存在1");
            throw new GlobalException(ErrorCodeEnum.LOGIN_ERROR);
        }
        //如果用户存在，则比对密码
        //此时传进来的密码是在前端已经加密、加盐的密码【md5(password 明文 + slat1)】
        //而数据库中的密码是【md5(md5(password 明文 + slat1) + slat2)】

        String dbPass = MD5Util.midPassToDBPass(password, user.getSlat());
        if (!dbPass.equals(user.getPassword())) {
            System.out.println("用户不存在2");
            throw new GlobalException(ErrorCodeEnum.LOGIN_ERROR);
        }
        //登录成功
        //给每个用户生成唯一的票据/键
        String ticket = UUIDUtil.uuid();
        //将用户信息存入 session 中
//        request.getSession().setAttribute(ticket, user);

        //为了实现分布式session存储，将session存放到redis
        redisTemplate.opsForValue().set("user:" + ticket,user);
        //将票据存入cookie中
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return BaseResponse.success(ticket);
    }

    /**
     * 根据cookie获取用户信息
     * @param userTicket session的key，根据 key 到 session 中获取对象信息
     * @param request request
     * @param response response
     * @return User
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        // 从redis中获取用户信息
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        // 如果用户信息不为空，则重置 cookie 的存活时间
        if (user != null) {
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        // 返回用户信息
        return user;
    }

    @Override
    public BaseResponse updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        //更新用户密码, 同时删除用户在 Redis 的缓存对象
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(ErrorCodeEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int i = userMapper.updateById(user);
        if (i == 1) {
            //删除 redis
            redisTemplate.delete("user:" + userTicket);
            return BaseResponse.success();
        }
        return BaseResponse.error(ErrorCodeEnum.PASSWORD_UPDATE_FAIL);
    }
}
