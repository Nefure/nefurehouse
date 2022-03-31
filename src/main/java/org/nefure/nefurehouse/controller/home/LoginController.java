package org.nefure.nefurehouse.controller.home;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author nefure
 * @date 2022/3/22 15:55
 */
@RestController
public class LoginController {

    @Resource
    private SystemConfigService systemConfigService;

    @PostMapping("/doLogin")
    public ResultData login(String userName, String passWord){
        SystemConfigDTO systemConfig = systemConfigService.getSystemConfig();
        if(Objects.equals(userName,systemConfig.getUsername()) && Objects.equals(SecureUtil.md5(passWord),systemConfig.getPassword())){
            StpUtil.login("admin");
            return ResultData.successData("登录成功");
        }
        return ResultData.error("登录失败");
    }

    @GetMapping("/logout")
    public ResultData logout(){
        StpUtil.logout();
        return ResultData.successData("注销成功");
    }
}
