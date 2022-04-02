package org.nefure.nefurehouse.model.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author nefure
 */
public class StorageTypeEnumJsonDeSerializerConvert extends JsonDeserializer<StorageType> {

    @Override
    public StorageType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return StorageType.getEnum(jsonParser.getText());
    }
}