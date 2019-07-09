package com.shadwo.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import com.shadow.gmall.service.UmsAddressService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UmsAddressHandler {
    @Reference
    private UmsAddressService umsAddressService;
    @RequestMapping("umsAddress/index")
    public String test(){
        return "hello word!!!";
    }

    @RequestMapping("umsAddress/get/all.html")
    public String getAll(){
        StringBuilder str=new StringBuilder();
        List<UmsMemberReceiveAddress> all = this.umsAddressService.getAll();
       for(UmsMemberReceiveAddress ums:all){
           System.out.println(ums);
           str.append(ums.toString());
       }
        return str.toString();
    }
    @RequestMapping("umsAddress/get/ums/by/{id}")
    public String getUmsById(@PathVariable("id") String id){
        UmsMemberReceiveAddress umsById = this.umsAddressService.getUmsById(id);
        return umsById.toString();
    }

    @RequestMapping("umsAddress/del/ums/by/{id}")
    public String delUmsById(@PathVariable("id") String id){
        Integer i=this.umsAddressService.delUmsById(id);
        return i.toString();

    }
    @RequestMapping("umsAddress/updata/ums/with/ums")
    public String updataUms(UmsMemberReceiveAddress umsMemberReceiveAddress){
        Integer i=this.umsAddressService.updataUms(umsMemberReceiveAddress);
        if(0==i){
            return "false";
        }
        return "true";
    }
    @RequestMapping("umsAddress/query/ums/address/by/umsMember/{id}")
    public String queryUmsAddress(@PathVariable("id") String id){
        UmsMemberReceiveAddress umsinfo=this.umsAddressService.queryUmsMemberById(id);
        return umsinfo.toString();

    }
}
