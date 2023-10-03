package org.nefure.nefurehouse.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询文件目录时，根据是否开启缓存来决定是否从源获取文件表
 * @author nefure
 * @date 2022/3/31 15:44
 */
@Aspect
@Component
public class CacheAspect {

    @Resource
    private HouseCache houseCache;

    @Resource
    private DriverConfigService driverConfigService;

    @SuppressWarnings("unchecked")
    @Around("execution(public * org.nefure.nefurehouse.service.base.AbstractBaseFileService.fileList(..))")
    public Object aroundFileList(ProceedingJoinPoint point) throws Throwable {
        //获取要查找的文件路径
        Object[] args = point.getArgs();
        String path = String.valueOf(args[0]);

        AbstractBaseFileService fileService = (AbstractBaseFileService) point.getTarget();
        Long driveId = fileService.getDriveId();

        DriverConfig driverConfig = driverConfigService.getDriverConfigById(driveId);
        //判断开了缓存没
        //没有开缓存就直接跑原方法
        if(null == driverConfig || !driverConfig.getEnable() || !driverConfig.getEnableCache()){
            return point.proceed();
        }
        //开了就直接取缓存里的（缓存没有就跑原方法放进去）
        List<FileItemDTO> cache = houseCache.get(driveId, path);
        if(cache == null){
            cache = (List<FileItemDTO>) point.proceed();
            houseCache.put(driveId,path,cache);
        }
        //返回缓存的副本
        return new ArrayList<>(cache);
    }
}
