package org.nefure.nefurehouse.controller.admin;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ZipUtil;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.nefure.nefurehouse.util.FileUtil;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * 后台管理接口，用于修改配置信息
 * @author nefure
 * @date 2022/3/27 13:41
 */
@RestController
@RequestMapping("/admin")
public class ConfigController {

    @Resource
    private SystemConfigService service;

    @GetMapping("/config")
    public ResultData getSystemConfig(){
        return ResultData.successData(service.getSystemConfig());
    }

    @PostMapping("/config")
    public ResultData updateSystemConfig(SystemConfigDTO systemConfigDTO){
        systemConfigDTO.setId(1);
        service.updateSystemConfig(systemConfigDTO);
        return ResultData.success();
    }

    @PostMapping("/update-pwd")
    public ResultData updatePassWord(String username, String password){
        service.updateUsernameAndPwd(username,password);
        return ResultData.success();
    }

    @GetMapping("/log")
    public ResponseEntity<?> logDownload(){
        String tmpLogPath = HouseConstant.TMP_FILE_PATH;
        File zip = ZipUtil.zip(tmpLogPath);
        String curTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        return FileUtil.exportSingleThread(zip, "ZFile 诊断日志 - " + curTime + ".zip");
    }
}
