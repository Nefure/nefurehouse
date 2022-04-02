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

    @PostMapping("/{driveId}/refresh")
    public ResultData refreshCache(@PathVariable Integer driveId, String key) throws Exception {
        driverConfigService.refreshCache(driveId,key);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/auto-refresh/start")
    public ResultData startAutoRefresh(@PathVariable Integer driveId){
        driverConfigService.updateAutoRefreshStatus(driveId,true);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/auto-refresh/stop")
    public ResultData stopAutoRefresh(@PathVariable Integer driveId){
        driverConfigService.updateAutoRefreshStatus(driveId,false);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/clear")
    public ResultData clearCache(@PathVariable Integer driveId){
        driverConfigService.clearCache(driveId);
        return ResultData.success();
    }
}
