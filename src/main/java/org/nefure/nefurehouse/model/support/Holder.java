package org.nefure.nefurehouse.model.support;

import lombok.Data;

/**
 * @author nefure
 * @CreateTime 2023年10月07日 00:07:00
 */
@Data
public class Holder<T> {

    private volatile T value;
}
