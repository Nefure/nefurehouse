package org.nefure.nefurehouse.service.base;

import org.nefure.nefurehouse.model.enums.StorageType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author nefure
 * @date 2022/1/1 16:53
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HuaweiFileService extends S3FileService{
    @Override
    public StorageType getType() {
        return StorageType.HUAWEI;
    }


    @Override
    public String getRegion() {
        return "obs";
    }
}
