package org.nefure.nefurehouse.controller.home;

import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 存储策略相关接口
 * @author nefure
 * @date 2022/3/29 14:56
 */
@RestController
@RequestMapping("/admin")
public class SupportController {

    @Resource
    private DriveContext driveContext;

    @GetMapping("/support-strategy")
    public ResultData getSupportStrategies(){
        return ResultData.successData(StorageType.values());
    }

    @GetMapping("/strategy-form")
    public ResultData getForm(StorageType storageType){
        AbstractBaseFileService storageService = driveContext.getBeanByStorageType(storageType);
        List<StorageConfig> storageConfigs = storageService.storageStrategyConfigList();
        return ResultData.successData(storageConfigs);
    }
}
