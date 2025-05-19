package com.sean.seckill.controller;


import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.vo.GoodsVo;
import com.sean.seckill.service.GoodsService;
import com.sean.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;
    //手动渲染
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList")
    public String toList(Model model, @CookieValue("userTicket") String ticket,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        //如果 cookie 没有生成
        if (StringUtils.isBlank(ticket)) {
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
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    //这里的 user 对象通过MVC解析器已经封装了
    @Deprecated
    @RequestMapping(value = "/toList/mvc")
    public String toListByMVC(Model model, User user) {
        //如果用户没有登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }

    /**
     * 使用 redis 缓存页面
     * @param model 存储数据
     * @param user 用户信息
     * @param request request
     * @param response response
     * @return html
     */
    @RequestMapping(value = "/toList/redis", produces = "text/html;charset=utf-8")
    @ResponseBody //使用了 redis 缓存页面需要添加
    @Deprecated
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        //先从 redis 中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.isNotBlank(html)) {
            return html;
        }
        //如果用户没有登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        //展示商品
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        //return "goodsList";

        //如果为从 redis 中取出的页面为 null，手动渲染，存入 redis 中
        WebContext webContext = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver. getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.isNotBlank(html)) {
            //每 60s 更新一次 redis 页面缓存, 即 60s 后, 该页面缓存失效, Redis 会清除该页面缓存
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }


    @RequestMapping(value = "/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        //把秒杀倒计时和秒杀状态给我们的模板使用
        //秒杀状态：0-未开始 1-已经开始 2-已经结束

        //开始时间
        Date startDate = goodsVo.getStartDate();
        //结束时间
        Date endDate = goodsVo.getEndDate();
        //当前时间
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时 -s
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            //秒杀还没有开始
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        System.out.println("secKillStatus=" + secKillStatus);
        System.out.println("remainSeconds=" + remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goodsDetail";
    }
}
