package org.nefure.nefurehouse.controller.admin;

import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author nefure
 * @date 2022/3/24 15:27
 */
@RestController
public class DebugController {
    @Value("${nefurehouse.debug}")
    private Boolean debug;

    @Resource
    private SystemConfigService service;

    @GetMapping("/debug/resetPwd")
    public ResultData resetPwd(){
        if (debug){
            service.updateUsernameAndPwd("misaki","nefure");
            return ResultData.success();
        }else {
            return ResultData.error("只有在 debug 模式才能进行此操作哦");
        }
    }
}
