package org.nefure.nefurehouse.model.entity;

import lombok.Data;
import org.nefure.nefurehouse.model.enums.StorageType;

/**
 * 存储策略的每项属性的信息
 * @author nefure
 * @date 2022/3/21 17:35
 */
@Data
public class StorageConfig {

    private Integer id;

    private StorageType type;

    private String key;

    private String title;

    private String value;

    private Integer driveId;

    public StorageConfig(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public void setType(String type) {
        this.type = StorageType.getEnum(type);
    }

    public StorageConfig(){}

    public void setTypeEnum(StorageType type) {
        this.type = type;
    }
}
