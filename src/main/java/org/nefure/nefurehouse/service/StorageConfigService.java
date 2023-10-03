package org.nefure.nefurehouse.service;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.mapper.StorageConfigMapper;
import org.nefure.nefurehouse.model.dto.StorageStrategyConfig;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nefure
 * @date 2022/3/21 16:07
 */
@Slf4j
@Service
public class StorageConfigService {

    @Resource
    private StorageConfigMapper storageConfigMapper;

    public Map<String, StorageConfig> getStringStorageConfigMap(Long driveId) {
        Map<String,StorageConfig> stringStorageConfigMap = new HashMap<>();
        for (StorageConfig storageConfig : findStorageConfigByDriveId(driveId)){
            stringStorageConfigMap.put(storageConfig.getKey(),storageConfig);
        }
        return stringStorageConfigMap;
    }

    public List<StorageConfig> findStorageConfigByDriveId(Long driveId){
        return storageConfigMapper.findByDriveId(driveId);
    }

    public StorageStrategyConfig getStorageStrategyConfigByDriveId(Long driveId){
        List<StorageConfig> storageConfigs = findStorageConfigByDriveId(driveId);
        return toStorageStrategyConfig(storageConfigs);
    }

    public void deleteByDriveId(Long driveId) {
        storageConfigMapper.deleteByDriveId(driveId);
    }

    public StorageStrategyConfig toStorageStrategyConfig(List<StorageConfig> storageConfigs){
        StorageStrategyConfig storageStrategyConfig = new StorageStrategyConfig();
        Class<StorageStrategyConfig> aClass = StorageStrategyConfig.class;
        for(StorageConfig config : storageConfigs){
            String key = config.getKey();
            String value = config.getValue();
            try {
                Field field = aClass.getDeclaredField(key);
                field.setAccessible(true);
                if(field.getType().equals(Boolean.class)){
                    field.set(storageStrategyConfig,Boolean.valueOf(value));
                }
                else {
                    field.set(storageStrategyConfig,value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("storageStrategyConfig注入字段{}时发生异常：{}",key,e.getMessage());
            }
        }
        return storageStrategyConfig;
    }

    public void insert(List<StorageConfig> storageConfigs) {
        storageConfigMapper.insert(storageConfigs);
    }
}
