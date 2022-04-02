package org.nefure.nefurehouse.model.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.model.enums.StorageTypeEnumJsonDeSerializerConvert;
import org.nefure.nefurehouse.model.enums.StorageTypeEnumJsonSerializerConvert;

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

    @JsonDeserialize(using = StorageTypeEnumJsonDeSerializerConvert.class)
    private StorageType type;

    private Boolean searchEnable;

    private Boolean searchIgnoreCase;

    private Boolean searchContainEncryptedFile;

    private Integer orderNum;

    private Boolean defaultSwitchToImgMode;

    public DriverConfig(){}

}
