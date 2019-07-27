package com.shadow.gmall.password.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.Utils.CookieUtil;
import com.shadow.gmall.Utils.HttpclientUtil;
import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.beans.UmsMember;
import com.shadow.gmall.password.Util.JwtUtil;
import com.shadow.gmall.service.UmsMemberService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SSOHandler {

    @Reference
    private UmsMemberService umsMemberService;


    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap map){
        //让登陆页面知道使从哪里跳转来到
        map.addAttribute("originUrl",ReturnUrl);
        return "index";
    }



    //页面登陆，进行数据库查询，查询成功返回有效token，查询失败返回无效token
    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request){
        String token="fail";

        //根据参数查找数据库，是否存在该用户
        UmsMember umsMemberFromdb=this.umsMemberService.getUmsMemberFromdb(umsMember);
        if(umsMemberFromdb==null){
            //数据库中没有数据，则需要跳转去注册页面
            return token;
        }

        //获取发送请求的客户端的ip
        //请求转发
        String salt = request.getHeader("x-forward-for");
        if(StringUtils.isBlank(salt)){
            //没有请求转发时
            salt = request.getRemoteAddr();
            if(StringUtils.isBlank(salt)){
                return token;
            }
            //salt="127.0.0.1";
        }
        //token中携带的信息
        String memberId=umsMemberFromdb.getId()+"";

        Map<String,Object> map=new HashMap<>();
        map.put("nickname",umsMemberFromdb.getNickname());
        map.put("memberId",memberId);
        map.put("success","success");
        //存在，生成token，将token返回,
        token = JwtUtil.encode("gmall", map, salt);
        //同步缓存,结构为user:id:token-------token
        this.umsMemberService.putUserToCache(token,memberId);


        return token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public Map<String,String> verify(String token,HttpServletRequest request,String salt){
        Map<String,String> mapstr=new HashMap<>();
        //获取发送请求的客户端的ip
//        //请求转发
//        String salt = request.getHeader("x-forward-for");
//        if(StringUtils.isBlank(salt)){
//            //没有请求转发时
//            salt = request.getRemoteAddr();
//            if(StringUtils.isBlank(salt)){
//                mapstr.put("success","fail");
//                return mapstr;
//            }
//        }
        //验证token是否有效
        Map<String, Object> map = JwtUtil.decode(token,"gmall",salt);
        if(map==null){
            //解析失败
            mapstr.put("success","fail");
            return mapstr;
        }
        //解析成功
        mapstr.put("success","success");
        mapstr.put("nickname",(String)map.get("nickname"));
        mapstr.put("memberId",(String)map.get("memberId"));

        return mapstr;
    }

    @RequestMapping("vlogin")
    public String vlogin(String code,HttpServletRequest request){

        //页面发送请求后，获得的weibo返回的
        String key = "4072006538";
        String secret="4bc1801e459dfc950e7d1b38fc893e97";
        String url="https://api.weibo.com/oauth2/access_token";
        Map<String,String> mapsend=new HashMap<>();
        mapsend.put("client_id","4072006538");
        mapsend.put("client_secret","4bc1801e459dfc950e7d1b38fc893e97");
        mapsend.put("grant_type","authorization_code");
        mapsend.put("redirect_uri","http://127.0.0.1:8087/vlogin");
        mapsend.put("code",code);
        String jsonFromWB = HttpclientUtil.doPost(url, mapsend);
        //解析失败让其回到首页，没有token
        if(StringUtils.isBlank(jsonFromWB)){
            return "redirect:http://localhost:8084/index";
        }
        HashMap hashMap = JSON.parseObject(jsonFromWB, new HashMap<String, Object>().getClass());
        String accessToken  = (String)hashMap.get("access_token");
        String uid = (String)hashMap.get("uid");
        String userJsonFromWB = HttpclientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token=" + accessToken+"&uid="+uid);
        HashMap userHashMap = JSON.parseObject(userJsonFromWB, new HashMap<String, Object>().getClass());
        System.out.println(userHashMap.toString());
        //获取用户注册在web端的信息，形成联合账号，插入自己的数据库
        UmsMember umsMember=new UmsMember();
        umsMember.setNickname((String)userHashMap.get("screen_name"));
        umsMember.setUsername((String)userHashMap.get("name"));
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(accessToken);
        umsMember.setSourceUid((String)userHashMap.get("idstr"));
        umsMember.setCreateTime(new Date());
        umsMember.setGender("m".equals(userHashMap.get("gender"))?"1":"2");
        umsMember.setSourceType("2");
        UmsMember umsMemberFromdb = this.umsMemberService.putUmsMemberTodb(umsMember);
        String token="fail";
        //获取发送请求的客户端的ip
        //请求转发
        String salt = request.getHeader("x-forward-for");
        if(StringUtils.isBlank(salt)){
            //没有请求转发时
            salt = request.getRemoteAddr();
            if(StringUtils.isBlank(salt)){
                return "redirect:http://localhost:8084/index";
            }
            //salt="127.0.0.1";
        }
        //token中携带的信息
        Map<String,Object> map=new HashMap<>();

        String memberId=umsMemberFromdb.getId()+"";
        map.put("memberId",memberId);
        map.put("nickname",umsMemberFromdb.getNickname());
        map.put("success","success");
        //存在，生成token，将token返回,
        token = JwtUtil.encode("gmall", map, salt);
        //同步缓存,结构为user:id:token-------token
        this.umsMemberService.putUserToCache(token,memberId);

        return "redirect:http://localhost:8084/index?token="+token;
    }



}
