package org.nefure.nefurehouse.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author nefure
 * @date 2022/3/20 19:38
 */
@Data
public class ShortLinkConfig {

    private Integer id;

    private String key;

    private String url;

    private Date createDate;
}
