package org.nefure.nefurehouse.service;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.exception.InvalidDriveException;
import org.nefure.nefurehouse.mapper.DriverConfigMapper;
import org.nefure.nefurehouse.mapper.FilterConfigMapper;
import org.nefure.nefurehouse.model.dto.DriverConfigDTO;
import org.nefure.nefurehouse.model.dto.StorageStrategyConfig;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.FilterConfig;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

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

    public DriverConfig getDriverConfigById(Integer driverId) {
        DriverConfig config = driverConfigDao.findById(driverId);
        if(config == null){
            throw new InvalidDriveException("此驱动器不存在或初始化失败, 请检查后台参数配置");
        }
        return config;
    }

    public List<DriverConfig> list() {
        return driverConfigDao.findAll("order_num",true);
    }

    public void updateDriveConfig(DriverConfig driverConfig) {
        driverConfigDao.update(driverConfig);
    }

    /**
     * 级联删除驱动
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer driveId) {
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

    public DriverConfigDTO getDriverConfigDTOById(Integer driveId) {
        DriverConfig driverConfig = getDriverConfigById(driveId);
        StorageStrategyConfig storageStrategyConfig = storageConfigService.getStorageStrategyConfigByDriveId(driveId);

        DriverConfigDTO driverConfigDTO = new DriverConfigDTO();
        BeanUtils.copyProperties(driverConfig,driverConfigDTO);
        driverConfigDTO.setStorageStrategyConfig(storageStrategyConfig);

        return driverConfigDTO;
    }

    public void updateDriveConfigDTO(DriverConfigDTO driverConfigDTO) {

    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDriveConfigDTO(DriverConfigDTO driverConfigDTO) {
        Integer driveId = driverConfigDTO.getId();
        DriverConfig driverConfig = new DriverConfig();
        StorageStrategyConfig storageStrategyConfig = driverConfigDTO.getStorageStrategyConfig();
        BeanUtils.copyProperties(driverConfigDTO,driverConfig);

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
    }

    private void insertDriverConfig(DriverConfig driverConfig) {
        driverConfigDao.save(driverConfig);
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
}
