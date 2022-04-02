package org.nefure.nefurehouse.model.enums;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nefure
 * @date 2022/3/13 9:34
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StorageType {
    /**
     * 当前系统支持的所有存储策略
     */
    LOCAL("local", "本地存储"),
    ALIYUN("aliyun", "阿里云 OSS"),
    TENCENT("tencent", "腾讯云 COS"),
    HUAWEI("huawei", "华为云 OBS");

    private String key;
    private String description;

    private static final Map<String,StorageType> MAP = new HashMap<>();

    static {
        for (StorageType type : StorageType.values()){
            MAP.put(type.key,type);
        }
    }

    StorageType(String k,String desc){
        key = k;
        description = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static StorageType getEnum(String key){
        return MAP.get(key.toLowerCase());
    }

}
