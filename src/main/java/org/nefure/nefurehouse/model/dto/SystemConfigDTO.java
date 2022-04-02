package org.nefure.nefurehouse.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.model.enums.StorageTypeEnumJsonSerializerConvert;

/**
 * @author nefure
 * @date 2022/3/18 17:40
 */
@Data
@ToString
public class SystemConfigDTO {

    @JsonIgnore
    private Integer id;

    private String siteName;

    private String username;

    @JsonSerialize(using = StorageTypeEnumJsonSerializerConvert.class)
    private StorageType storageStrategy;

    @JsonIgnore
    private String password;

    private String domain;

    private String customJs;

    private String customCss;

    private String tableSize;

    private Boolean showOperator;

    private Boolean showDocument;

    private String announcement;

    private Boolean showAnnouncement;

    private String layout;

    private Boolean showLinkBtn;

    private Boolean showShortLink;

    private Boolean showPathLink;

}
