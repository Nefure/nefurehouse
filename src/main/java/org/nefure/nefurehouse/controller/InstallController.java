package org.nefure.nefurehouse.controller;

import cn.hutool.crypto.SecureUtil;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author nefure
 * @date 2022/3/21 22:12
 */
@RestController
public class InstallController {

    @Resource
    private SystemConfigService systemConfigService;

    @GetMapping("/is-installed")
    public ResultData isInstalled(){
        if(!systemConfigService.isInstalled()){
            return ResultData.success();
        }
        return ResultData.error("请勿重复初始化！");
    }

    @PostMapping("/doInstall")
    public ResultData doInstalled(SystemConfigDTO systemConfigDTO){

        systemConfigDTO.setPassword(SecureUtil.md5(systemConfigDTO.getPassword()));
        systemConfigService.updateSystemConfig(systemConfigDTO);

        return ResultData.success();
    }
}
