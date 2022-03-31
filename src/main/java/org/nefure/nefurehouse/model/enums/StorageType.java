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
    UPYUN("upyun", "又拍云 USS"),
    FTP("ftp", "FTP"),
    UFILE("ufile", "UFile"),
    HUAWEI("huawei", "华为云 OBS"),
    MINIO("minio", "MINIO"),
    S3("s3", "S3通用协议"),
    ONE_DRIVE("onedrive", "OneDrive"),
    ONE_DRIVE_CHINA("onedrive-china", "OneDrive 世纪互联"),
    SHAREPOINT_DRIVE("sharepoint", "SharePoint"),
    SHAREPOINT_DRIVE_CHINA("sharepoint-china", "SharePoint 世纪互联"),
    QINIU("qiniu", "七牛云 KODO");

    private String key;
    private String description;

    private static Map<String,StorageType> map = new HashMap<>();

    static {
        for (StorageType type : StorageType.values()){
            map.put(type.key,type);
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
        return map.get(key.toLowerCase());
    }

}
