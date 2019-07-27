package com.shadow.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.shadow.gmall.Utils.CookieUtil;
import com.shadow.gmall.Utils.HttpclientUtil;
import com.shadow.gmall.myAnnotation.SSOAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class SSOinterceptor extends HandlerInterceptorAdapter{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取方法上的注解
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        SSOAnnotation methodAnnotation = handlerMethod.getMethodAnnotation(SSOAnnotation.class);

        //判断该方法是否需要登陆才能操作
        if(methodAnnotation==null){
            //不需要登陆就能操作
            System.out.println("不需要登录");
            return true;
        }
        //检查用户是否登陆，通过token
        String token="";
        //检查cookies中是否有oldtoken
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }

        //检查url参数中是否有newtoken
        String newToken = request.getParameter("newToken");
        if(StringUtils.isNotBlank(newToken)){
            token=newToken;
        }
        //需要登陆，却没有token，无权操作该功能，直接让其去登陆页面
        StringBuffer requestURL = request.getRequestURL();
        if(StringUtils.isBlank(token)){
            if(methodAnnotation.isNeedSuccess()){
                //判断是否必须要登陆才能操作
                //记录来之前的地址
                response.sendRedirect("http://localhost:8087/index?ReturnUrl="+requestURL.toString());
                return false;
            }
        }else {
            //获取请求的ip地址
            String salt = request.getHeader("x-forward-for");
            if(StringUtils.isBlank(salt)){
                //没有请求转发时
                salt = request.getRemoteAddr();
                if(StringUtils.isBlank(salt)){
                    return false;
                }
            }
            //验证token是否正确,调用认证中心的方法
            String success = HttpclientUtil.doGet("http://localhost:8087/verify?token=" + token+"&salt="+salt);
            HashMap hashMap = JSON.parseObject(success, new HashMap<String, String>().getClass());
            //token失效
            if ("fail".equals(hashMap.get("success"))) {
                //重新登陆
                response.sendRedirect("http://localhost:8087/index?ReturnUrl=" + requestURL.toString());
                return false;
            }else {


                //将token中的信息写入request域中
                request.setAttribute("memberId",hashMap.get("memberId"));
                request.setAttribute("nickname",hashMap.get("nickname"));
                //将token中的信息写入cookies中
                CookieUtil.setCookie(request,response,"oldToken",token,60*60,true);
            }
        }
        return true;
    }
}
