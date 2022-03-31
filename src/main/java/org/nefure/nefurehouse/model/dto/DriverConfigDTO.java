package org.nefure.nefurehouse.model.dto;

import lombok.ToString;
import org.nefure.nefurehouse.model.entity.DriverConfig;

/**
 * @author nefure
 * @date 2022/3/29 18:03
 */
@ToString
public class DriverConfigDTO extends DriverConfig{

    private StorageStrategyConfig storageStrategyConfig;

    public StorageStrategyConfig getStorageStrategyConfig() {
        return storageStrategyConfig;
    }

    public void setStorageStrategyConfig(StorageStrategyConfig storageStrategyConfig) {
        this.storageStrategyConfig = storageStrategyConfig;
    }
}
