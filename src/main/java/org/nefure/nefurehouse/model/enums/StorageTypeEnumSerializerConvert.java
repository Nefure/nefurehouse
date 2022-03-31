package org.nefure.nefurehouse.model.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author nefure
 * @date 2022/3/18 17:49
 */
public class StorageTypeEnumSerializerConvert extends JsonSerializer<StorageType> {

    @Override
    public void serialize(StorageType storageTypeEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(storageTypeEnum.getKey());
    }
}
