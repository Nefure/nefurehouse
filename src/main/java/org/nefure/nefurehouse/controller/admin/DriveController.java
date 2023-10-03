package org.nefure.nefurehouse.controller.admin;

import org.nefure.nefurehouse.model.dto.DriverConfigDTO;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.FilterConfig;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.nefure.nefurehouse.service.FilterConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 驱动管理接口
 * @author nefure
 * @date 2022/1/28 21:44
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class DriveController {

    @Resource
    private DriverConfigService driverConfigService;

    @Resource
    private FilterConfigService filterConfigService;

    /**
     * 获取所有驱动列表
     */
    @GetMapping("/drives")
    public ResultData getDrives(){
        return ResultData.successData(driverConfigService.list());
    }

    /**
     * 获取指定驱动信息
     */
    @GetMapping("/drive/{driveId}")
    public ResultData getDrive(@PathVariable Long driveId){
        return ResultData.successData(driverConfigService.getDriverConfigDTOById(driveId));
    }



    /**
     * 删除指定驱动
     */
    @DeleteMapping("/drive/{driveId}")
    public ResultData deleteDrive(@PathVariable Long driveId){
        driverConfigService.delete(driveId);
        return ResultData.success();
    }

    /**
     * 更新驱动
     */
    @PostMapping("/drive")
    public ResultData updateDrive(@RequestBody DriverConfigDTO driverConfigDTO){
        driverConfigService.saveDriveConfigDTO(driverConfigDTO);
        return ResultData.success();
    }

    /**
     * 启动指定驱动
     */
    @PostMapping("/drive/{driveId}/enable")
    public ResultData enableDrive(@PathVariable Long driveId){
        DriverConfig driverConfig = driverConfigService.getDriverConfigById(driveId);
        driverConfig.setEnable(true);
        driverConfigService.updateDriveConfig(driverConfig);
        return ResultData.success();
    }

    /**
     * 停用指定驱动
     */
    @PostMapping("/drive/{driveId}/disable")
    public ResultData disableDrive(@PathVariable Long driveId){
        DriverConfig driverConfig = driverConfigService.getDriverConfigById(driveId);
        driverConfig.setEnable(false);
        driverConfigService.updateDriveConfig(driverConfig);
        return ResultData.success();
    }

    /**
     * 获取过滤条件
     */
    @GetMapping("/drive/{driveId}/filters")
    public ResultData getFilters(@PathVariable Long driveId){
        return ResultData.successData(filterConfigService.getFiltersByDriveId(driveId));
    }

    @PostMapping("/drive/{driveId}/filters")
    public ResultData replaceFiltersByDriveId(@PathVariable Integer driveId, @RequestBody List<FilterConfig> filterConfigs){
        filterConfigService.replaceAllFilters(filterConfigs,driveId);
        return ResultData.success();
    }

    @PostMapping("/drive/drag")
    public ResultData dragDrive(@RequestBody List<DriverConfig> driverConfigList){
        driverConfigService.updateDriveOrderNumbers(driverConfigList);
        return ResultData.success();
    }

}
