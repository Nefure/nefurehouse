package org.nefure.nefurehouse.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.springframework.beans.BeanUtils;

/**
 * @author nefure
 * @date 2022/3/29 18:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class DriverConfigDTO extends DriverConfig{

    private StorageStrategyConfig storageStrategyConfig;

    public DriverConfigDTO(){}

}
