package com.shadow.gmall.ums.serviceimpl;

//import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.UmsMember;
import com.shadow.gmall.service.UmsMemberService;
import com.shadow.gmall.ums.mapper.UmsMemberMapper;
import com.shadow.gmall.ums.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Override
    public List<UmsMember> getAll() {
        return this.umsMemberMapper.getAll();
    }

    @Override
    public UmsMember getUmsById(String id) {
        UmsMember umsMember=new UmsMember();
        umsMember.setId(id);
        UmsMember umsMember1 = this.umsMemberMapper.selectOne(umsMember);
        return umsMember1;
    }

    @Override
    public Integer delUmsById(String id) {
       int i= this.umsMemberMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public Integer updataUms(UmsMember umsMember) {
        Integer i= this.umsMemberMapper.updateByPrimaryKeySelective(umsMember);
        return i;
    }

    @Override
    public UmsMember getUmsMemberFromdb(UmsMember umsMember) {
        return null;
    }

    @Override
    public void putUserToCache(String token, String id) {

    }

    @Override
    public UmsMember putUmsMemberTodb(UmsMember umsMember) {
        return null;
    }


}
