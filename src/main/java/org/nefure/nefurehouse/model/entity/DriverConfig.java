package org.nefure.nefurehouse.model.entity;


import lombok.Data;
import org.nefure.nefurehouse.model.enums.StorageType;

/**
 * @author nefure
 * @date 2022/3/12 21:24
 */
@Data
public class DriverConfig {

    private Integer id;

    private Boolean enable;

    private String name;

    private Boolean enableCache;

    private Boolean autoRefreshCache;

    private StorageType type;

    private Boolean searchEnable;

    private Boolean searchIgnoreCase;

    private Boolean searchContainEncryptedFile;

    private Integer orderNum;

    private Boolean defaultSwitchToImgMode;

    public void setType(String key){
        type = StorageType.getEnum(key);
    }
}
