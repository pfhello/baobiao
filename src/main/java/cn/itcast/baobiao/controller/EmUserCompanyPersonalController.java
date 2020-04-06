package cn.itcast.baobiao.controller;

import cn.itcast.baobiao.pojo.EmUserCompanyPersonal;
import cn.itcast.baobiao.service.EmUserCompanyPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class EmUserCompanyPersonalController {

    @Autowired
    private EmUserCompanyPersonalService emUserCompanyPersonalService;

    @GetMapping("/list")
    @ResponseBody
    public List<EmUserCompanyPersonal> getEmUserCompanyPersonalList(){
        return emUserCompanyPersonalService.getEmUserCompanyPersonalList();
    }
}
