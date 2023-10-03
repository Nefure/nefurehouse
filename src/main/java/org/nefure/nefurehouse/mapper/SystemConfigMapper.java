package org.nefure.nefurehouse.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.nefure.nefurehouse.model.entity.SystemConfig;

import java.util.List;
import java.util.Set;

/**
 * @author nefure
 * @date 2022/3/22 17:33
 */
public interface SystemConfigMapper {

    String TABLE_NAME = "system_config";


    /**
     * 从数据库查找系统配置的各项信息
     *
     * @return 系统配置信息
     */
    @Select({"select * from", TABLE_NAME})
    List<SystemConfig> findAll();


    /**
     * 通过参数名来获取系统配置的参数项
     *
     * @param key 参数名
     * @return 系统配置项
     */
    @Select({"select * from", TABLE_NAME, "where `key`=#{key}"})
    SystemConfig findByKey(String key);

    /**
     * 通过配置项的id获取配置项
     * @param id id
     * @return 匹配的配置项
     */
    @Select({"select * from",TABLE_NAME,"where id=#{id}"})
    SystemConfig getById(Integer id);

    /**
     * 通过id批量获取配置项
     * @param ids id集合
     * @return 匹配项的集合
     */
    @Select({"<script>select * from",TABLE_NAME,"where id in(<foreach collection='list' item='id' separator=','>#{id}</foreach>,null)</script>"})
    List<SystemConfig> getByIds(List<Integer> ids);

    /**
     * 通过id批量检查配置项是否存在于数据库
     * @param ids id集合
     * @return 匹配项的set集合
     */
    @Select({"<script>select id from",TABLE_NAME,"where id in(<foreach collection='list' item='id' separator=','>#{id}</foreach>,null)</script>"})
    Set<Integer> findByIds(List<Integer> ids);

    /**
     * 插入配置项
     *传入的list必须校验是否为空
     * @param systemConfigs 配置项（可以批量插入）
     */
    @Insert({"<script>",
            "<foreach collection='list' item='config' open='", "insert", TABLE_NAME, "(`key`,`value`,remark) values", "' separator=','>",
            "(#{config.key},#{config.value},#{config.remark})",
            "</foreach>",
            "</script>"})
    void saveAll(List<SystemConfig> systemConfigs);


    /**
     * 通过id批量修改配置项
     * @param systemConfigs 要修改的配置项，！！必须至少有一个元素的id存在
     * @return 影响行数
     */
    @Update({"<script>",
            "<foreach collection='list' item='config' separator=';'>",
            "<if test='config!=null and config.id!=null'>",
            "update",TABLE_NAME,
            "<set>",
            "id=#{config.id}","<if test='config.key!=null'>,`key`=#{config.key}</if><if test='config.value!=null'>,`value`=#{config.value}</if><if test='config.remark!=null'>,remark=#{config.remark}</if>",
            "</set>",
            "where id=#{config.id}",
            "</if>",
            "</foreach>",
            "</script>"})
    int updateAll(List<SystemConfig> systemConfigs);

    /**
     * 通过id批量修改配置项
     * @param systemConfigs 要修改的项
     * @return 影响行数
     */
    @Update({"<script>",
            "<foreach collection='array' item='config' separator=';'>",
            "<if test='config!=null and config.id!=null'>",
            "update",TABLE_NAME,
            "<set>",
            "id=#{config.id}","<if test='config.key!=null'>,`key`=#{config.key}</if><if test='config.value!=null'>,`value`=#{config.value}</if><if test='config.remark!=null'>,remark=#{config.remark}</if>",
            "</set>",
            "where id=#{config.id}",
            "</if>",
            "</foreach>",
            "</script>"})
    int update(SystemConfig... systemConfigs);

}
