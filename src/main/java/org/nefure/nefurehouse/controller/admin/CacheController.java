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
@CrossOrigin
@RestController
@RequestMapping("/admin/cache")
public class CacheController {

    @Resource
    private DriverConfigService driverConfigService;

    @PostMapping("/{driveId}/enable")
    public ResultData enableCache(@PathVariable Long driveId){
        driverConfigService.updateDriveCacheStatus(driveId,true);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/disable")
    public ResultData disableCache(@PathVariable Long driveId){
        driverConfigService.updateDriveCacheStatus(driveId,false);
        return ResultData.success();
    }

    @GetMapping("/{driveId}/info")
    public ResultData getCacheInfo(@PathVariable Long driveId){
        CacheInfoDTO cacheInfoDTO = driverConfigService.getCacheInfo(driveId);
        return ResultData.successData(cacheInfoDTO);
    }

    @PostMapping("/{driveId}/refresh")
    public ResultData refreshCache(@PathVariable Long driveId, String key) throws Exception {
        driverConfigService.refreshCache(driveId,key);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/auto-refresh/start")
    public ResultData startAutoRefresh(@PathVariable Long driveId){
        driverConfigService.updateAutoRefreshStatus(driveId,true);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/auto-refresh/stop")
    public ResultData stopAutoRefresh(@PathVariable Long driveId){
        driverConfigService.updateAutoRefreshStatus(driveId,false);
        return ResultData.success();
    }

    @PostMapping("/{driveId}/clear")
    public ResultData clearCache(@PathVariable Long driveId){
        driverConfigService.clearCache(driveId);
        return ResultData.success();
    }
}
