package org.nefure.nefurehouse.service.base;

import org.nefure.nefurehouse.model.enums.StorageType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author nefure
 * @date 2022/4/1 16:52
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TencentFileService extends S3FileService{
    @Override
    public StorageType getType() {
        return StorageType.TENCENT;
    }

    @Override
    public String getRegion() {
        return "cos";
    }
}
