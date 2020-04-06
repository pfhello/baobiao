package cn.itcast.baobiao.service.impl;

import cn.itcast.baobiao.mapper.EmUserCompanyPersonalMapper;
import cn.itcast.baobiao.pojo.EmUserCompanyPersonal;
import cn.itcast.baobiao.service.EmUserCompanyPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmUserCompanyPersonalServiceImpl implements EmUserCompanyPersonalService{

    @Autowired
    private EmUserCompanyPersonalMapper emUserCompanyPersonalMapper;

    @Override
    public List<EmUserCompanyPersonal> getEmUserCompanyPersonalList() {
        return emUserCompanyPersonalMapper.getEmUserCompanyPersonalList();
    }
}
