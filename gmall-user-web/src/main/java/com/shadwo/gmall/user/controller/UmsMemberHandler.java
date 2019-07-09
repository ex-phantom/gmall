package com.shadwo.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.UmsMember;
import com.shadow.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UmsMemberHandler {
    @Reference
    private UmsMemberService umsMemberService;
    @RequestMapping("umsMember/index")
    public String test(){
        return "hello word!!!";
    }

    @RequestMapping("umsMember/get/all.html")
    public String getAll(){
        StringBuilder str=new StringBuilder();
        List<UmsMember> all = this.umsMemberService.getAll();
       for(UmsMember ums:all){
           System.out.println(ums);
           str.append(ums.toString());
       }
        return str.toString();
    }
    @RequestMapping("umsMember/get/ums/by/{id}")
    public String getUmsById(@PathVariable("id") String id){
        UmsMember umsById = this.umsMemberService.getUmsById(id);
        return umsById.toString();
    }

    @RequestMapping("umsMember/del/ums/by/{id}")
    public String delUmsById(@PathVariable("id") String id){
        Integer i=this.umsMemberService.delUmsById(id);
        return i.toString();

    }
    @RequestMapping("umsMember/updata/ums/with/ums")
    public String updataUms(UmsMember umsMember){
        Integer i=this.umsMemberService.updataUms(umsMember);
        if(0==i){
            return "false";
        }
        return "true";
    }

    @RequestMapping("umsMember/query/ums/address/by/umsMember/{id}")
    public String queryUmsAddress(@PathVariable("id") String id){
        UmsMember umsinfo=this.umsMemberService.queryUmsAddressById(id);
        return umsinfo.toString();

    }

}
