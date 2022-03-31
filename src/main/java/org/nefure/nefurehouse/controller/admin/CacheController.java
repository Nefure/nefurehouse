package org.nefure.nefurehouse.controller.admin;

import org.nefure.nefurehouse.model.dto.CacheInfoDTO;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 缓存配置接口
 * @author nefure
 * @date 2022/3/31 13:34
 */
@RestController
@RequestMapping("/admin/cache")
public class CacheController {

    @Resource
    private DriverConfigService driverConfigService;

    @PostMapping("/{driveId}/enable")
    public ResultData enableCache(@PathVariable Integer driveId){
        driverConfigService.updateDriveCacheStatus(driveId,true);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/disable")
    public ResultData disableCache(@PathVariable Integer driveId){
        driverConfigService.updateDriveCacheStatus(driveId,false);
        return ResultData.success();
    }

    @GetMapping("/{driveId}/info")
    public ResultData getCacheInfo(@PathVariable Integer driveId){
        CacheInfoDTO cacheInfoDTO = driverConfigService.getCacheInfo(driveId);
        return ResultData.successData(cacheInfoDTO);
    }

}
