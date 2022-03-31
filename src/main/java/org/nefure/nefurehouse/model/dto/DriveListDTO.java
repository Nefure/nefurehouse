package org.nefure.nefurehouse.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nefure.nefurehouse.model.entity.DriverConfig;

import java.util.List;

/**
 * @author nefure
 * @date 2022/3/12 21:21
 */
@Data
@AllArgsConstructor
public class DriveListDTO {
    private List<DriverConfig> driveList;
    private Boolean isInstall;
}
