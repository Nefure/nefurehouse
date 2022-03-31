package org.nefure.nefurehouse.model.entity;

import lombok.Data;

/**
 * @author nefure
 * @date 2022/3/22 17:34
 */
@Data
public class SystemConfig {

    private Integer id;

    private String key;

    private String value;

    private String remark;
}
