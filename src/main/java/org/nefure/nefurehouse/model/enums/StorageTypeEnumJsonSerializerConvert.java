package org.nefure.nefurehouse.model.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author nefure
 * @date 2022/4/1 19:05
 */
public class StorageTypeEnumJsonSerializerConvert  extends JsonSerializer<StorageType> {

    @Override
    public void serialize(StorageType storageTypeEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(storageTypeEnum.getKey());
    }
}
