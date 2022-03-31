package org.nefure.nefurehouse.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.nefure.nefurehouse.model.entity.FilterConfig;

import java.util.List;

/**
 * @author nefure
 * @date 2022/3/20 15:34
 */
public interface FilterConfigMapper {

    String TABLE_NAME = "FILTER_CONFIG";

    /**
     * 通过驱动id获取此驱动的文件过滤方法
     * @param driveId 驱动id
     * @return 过滤配置
     */
    @Select({"select * from",TABLE_NAME,"where driver_id = #{driveId}"})
    List<FilterConfig> findByDriveId(Integer driveId);

    /**
     * 删除一个驱动的使用过滤选项
     * @param driveId 驱动id
     */
    @Delete("delete from "+TABLE_NAME+" where driveId=#{driveId}")
    void deleteByDriveId(Integer driveId);

    @Insert("insert "+TABLE_NAME+"(drive_id,expression) values(#{filterConfig.expression})")
    void save(FilterConfig filterConfig);
}
