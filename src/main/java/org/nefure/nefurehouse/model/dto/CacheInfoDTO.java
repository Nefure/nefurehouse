package org.nefure.nefurehouse.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * @author nefure
 * @date 2022/3/31 14:13
 */
@Data
@AllArgsConstructor
public class CacheInfoDTO {
    private Integer hitCount;
    private Integer missCount;
    private Integer cacheCount;
    private Set<String> cacheKeys;
}
