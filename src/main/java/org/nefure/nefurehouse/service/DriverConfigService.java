package org.nefure.nefurehouse.service;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.exception.InvalidDriveException;
import org.nefure.nefurehouse.mapper.DriverConfigMapper;
import org.nefure.nefurehouse.model.dto.DriverConfigDTO;
import org.nefure.nefurehouse.model.dto.StorageStrategyConfig;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.model.dto.CacheInfoDTO;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author nefure
 * @date 2022/3/13 10:13
 */
@Slf4j
@Service
public class DriverConfigService {

    @Resource
    private DriverConfigMapper driverConfigDao;

    @Resource
    private StorageConfigService storageConfigService;

    @Resource
    private HouseCache houseCache;

    @Resource
    private DriveContext driveContext;

    public List<DriverConfig> getOnlyEnable() {
        return driverConfigDao.findByEnabled(true, "order_num",true);
    }

    public DriverConfig getDriverConfigById(Long driverId) {
        DriverConfig config = driverConfigDao.findById(driverId);
        if(config == null){
            throw new InvalidDriveException("此驱动器不存在或初始化失败, 请检查后台参数配置");
        }
        return config;
    }

    public List<DriverConfig> list() {
        List<DriverConfig> all = driverConfigDao.findAll("order_num", true);
        return all;
    }

    public void updateDriveConfig(DriverConfig driverConfig) {
        driverConfigDao.update(driverConfig);
    }

    /**
     * 级联删除驱动
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long driveId) {
        if(log.isDebugEnabled()){
            log.debug("正在尝试删除drive,id：{}",driveId);
        }
        DriverConfig driverConfig = getDriverConfigById(driveId);
        driverConfigDao.delete(driveId);
        storageConfigService.deleteByDriveId(driveId);
        if(driverConfig.getEnable()){
            houseCache.stopAutoCacheRefresh(driveId);
            houseCache.clear(driveId);
        }
        driveContext.destory(driveId);
        if (log.isDebugEnabled()){
            log.debug("驱动删除成功,已清理驱动相关数据，driveId：{}",driveId);
        }
    }

    public DriverConfigDTO getDriverConfigDTOById(Long driveId) {
        DriverConfig driverConfig = getDriverConfigById(driveId);
        StorageStrategyConfig storageStrategyConfig = storageConfigService.getStorageStrategyConfigByDriveId(driveId);

        DriverConfigDTO driverConfigDTO = new DriverConfigDTO();
        BeanUtils.copyProperties(driverConfig,driverConfigDTO);
        driverConfigDTO.setType(driverConfig.getType());
        driverConfigDTO.setStorageStrategyConfig(storageStrategyConfig);

        return driverConfigDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDriveConfigDTO(DriverConfigDTO driverConfigDTO) {
        Long driveId = driverConfigDTO.getId();
        DriverConfig driverConfig = new DriverConfig();
        BeanUtils.copyProperties(driverConfigDTO,driverConfig);
        driverConfig.setType(driverConfigDTO.getType());
        StorageStrategyConfig storageStrategyConfig = driverConfigDTO.getStorageStrategyConfig();

        Class<StorageStrategyConfig> sClass = StorageStrategyConfig.class;
        StorageType type = driverConfigDTO.getType() == null ? StorageType.LOCAL : driverConfigDTO.getType();
        AbstractBaseFileService beanByStorageType = driveContext.getBeanByStorageType(type);
        List<StorageConfig> storageConfigs = beanByStorageType.storageStrategyConfigList();

        boolean isInsert = driveId == null;

        if(isInsert){
            insertDriverConfig(driverConfig);
            driveId = driverConfig.getId();
        }
        else {
            updateDriveConfig(driverConfig);
            storageConfigService.deleteByDriveId(driveId);
        }

        for (StorageConfig storageConfig : storageConfigs) {
            String key = storageConfig.getKey();

            try {
                Field field = sClass.getDeclaredField(key);
                field.setAccessible(true);
                Object o = field.get(storageStrategyConfig);
                storageConfig.setValue(o == null ? null : o.toString());
                storageConfig.setTypeEnum(type);
                storageConfig.setDriveId(driveId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("通过反射, 从 StorageStrategyConfig 中获取字段 {} 时出现异常:", key, e);
            }
        }
        storageConfigService.insert(storageConfigs);
        driveContext.init(driveId);
    }

    private void insertDriverConfig(DriverConfig driverConfig) {
        driverConfigDao.save(driverConfig);
        Long id = driverConfig.getId();
        driverConfigDao.updateDriveOrderNumber(id,id);
    }

    /**
     * 通过事务批量更新顺序
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDriveOrderNumbers(List<DriverConfig> driverConfigList) {
        for (DriverConfig driverConfig : driverConfigList){
            driverConfigDao.updateDriveOrderNumber(driverConfig.getId(),driverConfig.getOrderNum());
        }
    }

    public void updateDriveCacheStatus(Long driveId, boolean enable) {
        DriverConfig driverConfig = getDriverConfigById(driveId);
        if(driverConfig != null){
            driverConfig.setEnableCache(enable);
            updateDriveConfig(driverConfig);
        }
    }

    /**
     * 获取某个驱动的缓存信息
     */
    public CacheInfoDTO getCacheInfo(Long driveId) {
        int hitCount = houseCache.getHitCount(driveId);
        int missCount = houseCache.getMissCount(driveId);
        Set<String> keySet = houseCache.keySet(driveId);
        return new CacheInfoDTO(hitCount,missCount, keySet.size(), keySet);
    }

    /**
     * 手动刷新缓存
     */
    public void refreshCache(Long driveId, String key) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("手动刷新缓存 driveId: {}, key: {}", driveId, key);
        }
        houseCache.remove(driveId,key);
        AbstractBaseFileService fileService = driveContext.get(driveId);
        fileService.fileList(key);
    }

    /**
     * 开启驱动的缓存自动刷新
     */
    public void updateAutoRefreshStatus(Long driveId, boolean isStart) {
        DriverConfig driverConfig = driverConfigDao.findById(driveId);
        driverConfig.setAutoRefreshCache(isStart);
        driverConfigDao.update(driverConfig);
        if(isStart) {
            houseCache.startAutoCacheRefresh(driveId);
        }
        else {
            houseCache.stopAutoCacheRefresh(driveId);
        }
    }

    public void clearCache(Long driveId) {
        houseCache.clear(driveId);
    }
}
