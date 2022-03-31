package org.nefure.nefurehouse.mapper;

import org.apache.ibatis.annotations.*;
import org.nefure.nefurehouse.model.entity.DriverConfig;

import java.util.List;

/**
 * @author nefure
 * @date 2022/3/13 9:57
 */
public interface DriverConfigMapper {

    /**
     * 表名
     */
    String TABLE_NAME = "driver_config";
    /**
     * 按情况排序的sql片段
     */
    String ORDER = "<if test = 'orderBy != null'> order by #{orderBy}<choose><when test = 'asc != false'>asc</when> <otherwise>dec</otherwise></choose></if>";

    /**
     * 根据是否启用来查询驱动配置信息
     * @param enable 是否只查已启用的
     * @param asc 是否为正序，为null不进行排序
     * @return 查询结果
     */
    @Select({"<script>", "select * from", TABLE_NAME, "where enable=#{enable}", ORDER, "</script>"})
    List<DriverConfig> findByEnabled(Boolean enable, String orderBy, Boolean asc);

    /**
     * 根据id获取驱动配置
     * @param driverId id
     * @return 匹配的驱动配置
     */
    @Select({"select * from",TABLE_NAME,"where id = #{driverId}"})
    DriverConfig findById(Integer driverId);

    /**
     * 获取所有配置，可选是否排序
     * @return
     */
    @Select({"<script>", "select * from",TABLE_NAME,ORDER, "</script>"})
    List<DriverConfig> findAll(String orderBy, Boolean asc);

    /**
     * 插入新数据
     * @param driverConfig 新数据
     */
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    @Insert("insert "+TABLE_NAME+"(enable,name,enable_cache,auto_refresh_cache,type,search_enable,search_ignore_case,search_contain_encrypted_file,order_num,default_switch_to_img_mode) values(#{enable},#{name},#{enableCache},#{autoRefreshCache},#{type},#{searchEnable},#{searchIgnoreCase},#{searchContainEncryptedFile},#{orderNum},#{defaultSwitchToImgMode})")
    void save(DriverConfig driverConfig);
    /**
     * 更新数据
     * @param driverConfig 新数据
     */
    @Insert("update "+TABLE_NAME+" set enable=#{driverConfig.enable},name=#{driverConfig.name},enable_cache=#{driverConfig.enableCache},auto_refresh_cache=#{driverConfig.autoRefreshCache},type=#{driverConfig.type},search_enable=#{driverConfig.searchEnable},search_ignore_case=#{driverConfig.searchIgnoreCase},search_contain_encrypted_file=#{driverConfig.searchContainEncryptedFile},order_num=#{driverConfig.orderNum},default_switch_to_img_mode=#{driverConfig.defaultSwitchToImgMode})")
    void update(DriverConfig driverConfig);

    @Delete("delete from "+TABLE_NAME+" where id=#{driveId}")
    void delete(Integer driveId);

    /**
     * 更新顺序（在service层通过事务批量更新）
     * @param id 要更新顺序的驱动id
     * @param orderNum 要变更为的顺序
     */
    @Update("update "+TABLE_NAME+" set order_num=#{orderNum} where id=#{id}")
    void updateDriveOrderNumber(Integer id, Integer orderNum);
}
