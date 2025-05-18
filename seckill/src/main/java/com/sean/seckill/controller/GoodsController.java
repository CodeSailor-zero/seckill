package com.sean.seckill.controller;


import com.sean.seckill.model.dto.User;
import com.sean.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author sean
 * @Date 2025/01/18
 * 商品 Controller层
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private UserService userService;

    @RequestMapping(value = "/toList")
    public String toList(Model model, @CookieValue("userTicket") String ticket,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        //如果 cookie 没有生成
        if (!StringUtils.isBlank(ticket)) {
            return "login";
        }
//        User user = (User) session.getAttribute(ticket);
        // 从redis中获取用户数据
        User user = userService.getUserByCookie(ticket, request, response);
        //如果用户没有登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }

    //这里的 user 对象通过MVC解析器已经封装了
    @Deprecated
    @RequestMapping(value = "/toList")
    public String toListByMVC(Model model, User user) {
        //如果用户没有登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }
}
