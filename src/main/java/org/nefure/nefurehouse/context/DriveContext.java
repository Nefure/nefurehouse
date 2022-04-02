package org.nefure.nefurehouse.context;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.exception.InvalidDriveException;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.nefure.nefurehouse.util.SpringContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nefure
 * @date 2022/3/17 21:03
 */
@Slf4j
@DependsOn("springContextHolder")
@Component
public class DriveContext implements ApplicationContextAware {

    @Resource
    private DriverConfigService driverConfigService;

    private final Map<Integer,AbstractBaseFileService> serviceMap = new ConcurrentHashMap<>();

    private static Map<String, AbstractBaseFileService> fileServiceMap;

    /**
     * 通过驱动id获取对应service
     * @param driverId 驱动id
     * @return 能够对此驱动处理的service
     */
    public AbstractBaseFileService get(Integer driverId){
        AbstractBaseFileService abstractBaseFileService = serviceMap.get(driverId);
        if (abstractBaseFileService == null){
            throw new InvalidDriveException("此驱动器不存在或初始化失败, 请检查后台参数配置");
        }
        return abstractBaseFileService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        List<DriverConfig> driverConfigList = driverConfigService.list();
        for (DriverConfig driverConfig : driverConfigList) {
            try{
                init(driverConfig.getId());
                log.info("启动时初始化驱动器成功, 驱动器信息: {}", JSON.toJSONString(driverConfig));
            }catch (Exception e){
                log.error("启动时初始化驱动器失败, 驱动器信息: {}", JSON.toJSONString(driverConfig), e);
            }
        }
    }

    public void init(Integer driveId) {
        AbstractBaseFileService service = getBeanByDriverId(driveId);
        if(service != null){
            if(log.isDebugEnabled()){
                log.debug("尝试初始化驱动器, driveId: {}", driveId);
            }
            service.init(driveId);
            if(log.isDebugEnabled()){
                log.debug("初始化驱动器成功, driveId: {}", driveId);
            }
            serviceMap.put(driveId,service);
        }
    }

    /**
     * 根据驱动id得到能处理此驱动的service对象
     * @param driveId 驱动id
     * @return 对应的service对象
     */
    private AbstractBaseFileService getBeanByDriverId(Integer driveId) {
        DriverConfig driverConfig = driverConfigService.getDriverConfigById(driveId);
        if(driverConfig != null){
            Map<String, AbstractBaseFileService> beansOfType = SpringContextHolder.getBeansOfType(AbstractBaseFileService.class);
            for (AbstractBaseFileService service : beansOfType.values()) {
                if(Objects.equals(service.getType(),driverConfig.getType())){
                    return service;
                }
            }
        }
        return null;
    }

    public AbstractBaseFileService getBeanByStorageType(StorageType type){
        if (fileServiceMap == null) {
            fileServiceMap = SpringContextHolder.getBeansOfType(AbstractBaseFileService.class);
        }
        for (AbstractBaseFileService service : fileServiceMap.values()) {
            if(Objects.equals(service.getType(),type)) {
                return service;
            }
        }
        return null;
    }


    public void destory(Integer driveId) {
        serviceMap.remove(driveId);
    }
}
