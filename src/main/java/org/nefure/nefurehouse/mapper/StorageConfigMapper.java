package org.nefure.nefurehouse.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.nefure.nefurehouse.model.entity.StorageConfig;

import java.util.List;

/**
 * 每个存储类型配置
 * @author nefure
 * @date 2022/3/18 12:14
 */
public interface StorageConfigMapper {

    String TABLE_NAME = "storage_config";

    /**
     * 获取一个驱动所有的存储属性
     * @param driveId 驱动id
     * @return 存储配置
     */
    @Select({"select * from",TABLE_NAME,"where drive_id=#{driveId}"})
    List<StorageConfig> findByDriveId(Integer driveId);

    /**
     * 删除某个驱动相关的存储信息
     * @param driveId 驱动id
     */
    @Delete("delete from "+TABLE_NAME+" where drive_id=#{driveId]")
    void deleteByDriveId(Integer driveId);

    /**
     *
     * @param storageConfigs
     */
    @Insert({"<script>",
            "<foreach collection='list' item='config' open='", "insert", TABLE_NAME, "(`key`,`value`,`title`,`driveId`,`type`) values", "' separator=','>",
            "(#{config.key},#{config.value},#{config.title},#{config.driveId},#{config.type})",
            "</foreach>",
            "</script>"})
    void insert(List<StorageConfig> storageConfigs);
}
