package org.nefure.nefurehouse.model.entity;

import lombok.Data;

/**
 * @author nefure
 * @date 2022/3/20 15:32
 */
@Data
public class FilterConfig {

    private Integer id;

    private Integer driveId;

    private String expression;
}
