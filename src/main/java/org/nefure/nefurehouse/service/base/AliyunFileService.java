package org.nefure.nefurehouse.service.base;

import org.nefure.nefurehouse.model.enums.StorageType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author nefure
 * @date 2022/4/1 16:49
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AliyunFileService extends S3FileService{
    @Override
    public StorageType getType() {
        return StorageType.ALIYUN;
    }

    @Override
    public String getRegion() {
        return "oss";
    }
}
