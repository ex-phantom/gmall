package com.shadow.gmall.ums.controller;

//import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import com.shadow.gmall.service.UmsAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UmsAddressHandler {
    private int i=0;
    @Autowired
    private UmsAddressService umsAddressService;
    @RequestMapping("index")
    public void index(){

        System.out.println(++i+"=i");
    }




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

    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
    @RequestMapping("umsAddress/get/ums/by/{id}")
    public String getUmsById(@PathVariable("id") String id){
        UmsMemberReceiveAddress umsById = this.umsAddressService.getUmsById(id);
        return umsById.toString();
    }
    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
    @RequestMapping("umsAddress/del/ums/by/{id}")
    public String delUmsById(@PathVariable("id") String id){
        Integer i=this.umsAddressService.delUmsById(id);
        return i.toString();

    }
    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
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
