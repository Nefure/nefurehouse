package org.nefure.nefurehouse.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nefure
 * @date 2022/3/20 17:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileListDTO {

    private List<FileItemDTO> files;

    private SystemFrontConfigDTO config;
}
