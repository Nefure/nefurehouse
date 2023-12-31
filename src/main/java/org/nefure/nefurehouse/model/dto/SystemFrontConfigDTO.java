package org.nefure.nefurehouse.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @author nefure
 * @date 2022/3/20 16:25
 */
@Data
@ToString
public class SystemFrontConfigDTO{

    @JsonIgnore
    private Integer id;

    private String siteName;

    private Boolean searchEnable;

    private String username;

    private String domain;

    private String customJs;

    private String customCss;

    private String tableSize;

    private Boolean showOperator;

    private Boolean showDocument;

    private String announcement;

    private Boolean showAnnouncement;

    private String layout;

    private String readme;

    private Boolean debugMode;

    private Boolean defaultSwitchToImgMode;

    private Boolean showLinkBtn;

    private Boolean showShortLink;

    private Boolean showPathLink;

    private String directLinkPrefix;

    public SystemFrontConfigDTO(){}

    public SystemFrontConfigDTO(SystemConfigDTO systemConfigDTO){
        BeanUtils.copyProperties(systemConfigDTO,this);
    }
}
