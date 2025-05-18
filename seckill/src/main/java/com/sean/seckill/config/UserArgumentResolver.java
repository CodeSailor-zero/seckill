package com.sean.seckill.config;

import com.sean.seckill.model.dto.User;
import com.sean.seckill.service.UserService;
import com.sean.seckill.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sean
 * @Date 2025/26/18
 * 用户参数解析器
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Resource
    private UserService userService;

    //解析判断器
    //判断你当前要解析的类型是否是你需要的类型
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 获取参数类型
        Class<?> aClass = methodParameter.getParameterType();
        //判断参数类型是User类型，为 T，则进入 resolveArgument() 方法
        return aClass == User.class;
    }

    //解析器
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        //获取 cookie 的用户票据
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        //如果用户票据为空，说明用户没有登录。
        if (StringUtils.isBlank(userTicket)) {
            return null;
        }
        //根据用户票据到redis获取用户信息
        return userService.getUserByCookie(userTicket, request, response);
    }
}
