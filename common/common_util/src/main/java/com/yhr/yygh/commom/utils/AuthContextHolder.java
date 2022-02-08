package com.yhr.yygh.commom.utils;

import com.yhr.yygh.commom.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

//获取当前用户信息工具类
public class AuthContextHolder {

    //获取当前用户id
    public static Long getUserId(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    //获取当前用户名称
    public static String getUserName(HttpServletRequest request){
        String token = request.getHeader("token");
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
