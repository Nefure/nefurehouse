package org.nefure.nefurehouse.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.service.ShortLinkService;

import java.util.List;

/**
 * @author nefure
 * @date 2022/3/20 20:09
 */
public interface ShortLinkMapper {
    String TABLE_NAME = "short_link_config";

     enum column{
        /**
         * 列名
         */
        ID("`id`"),
        KEY("`key`"),
        URL("`url`"),
        CREATEDATE("`createDate`");
        public final String name;
        column(String col){
            name = col;
        }
     }

    /**
     * 通过url找短链接
     * @param url 全路径
     * @return 已配置的短链接
     */
    @Select({"select * from "+TABLE_NAME+" where url = #{url}"})
    ShortLinkConfig findByUrl(String url);

    /**
     *  添加记录
     * @param shortLinkConfig 插入对象实体
     */
    @Insert("insert "+TABLE_NAME+" set `key`=#{key},url=#{url}")
    void save(ShortLinkConfig shortLinkConfig);

    /**
     * 获取单个配置项
     * @param key 短链
     * @return 匹配的配置项
     */
    @Select({"select * from",TABLE_NAME,"where `key`=#{key}"})
    ShortLinkConfig getByKey(String key);

    /**
     * 通过id获取配置
     * @param id 要找的项id
     * @return 匹配项
     */
    @Select({"select * from",TABLE_NAME,"where id=#{id}"})
    ShortLinkConfig getById(Integer id);

    /**
     * （可批量）删除
     * @param ids 要删除项的id
     */
    @Delete({"<script>delete from",TABLE_NAME,"where id in(<foreach collection='array' item='id' separator=','>#{id}</foreach>)</script>"})
    void delete(Integer... ids);

    /**
     *获取所有
     * @return 所有项
     */
    List<ShortLinkService> findAll();

    /**
     * 获取记录总数
     * @return 记录总数
     */
    @Select("select count(id) from "+TABLE_NAME)
    long count();

    /**
     * 必须在调用前检查排序字段（防止sql注入）
     * 条件查询所有匹配项
     * @param key 短链
     * @param url 直链
     * @param dateFrom 开始时间
     * @param dateTo 结束时间
     * @param number 起始页
     * @param size 页长
     * @param orderBy 排序字段
     * @param desc 是否降序
     * @return 匹配页的数据
     */
    @Select({"<script>select * from",TABLE_NAME,
            "<where>" +
                    "<if test='key!=null'>and `key`like #{key}</if>" +
                    "<if test='url!=null'>and url like #{url}</if>" +
                    "<if test='dateFrom!=null'>and createDate &gt;= #{dateFrom}</if>" +
                    "<if test='dateTo!=null'>and createDate &lt;= #{dateTo}</if>" +
            "</where>" +
                    "<if test='orderBy!=null'>order by ${orderBy} <if test='desc'>desc</if> </if>" +
                    "<if test='number!=null and size != null'>limit #{number},#{size}</if>" +
            "</script>"})
    List<ShortLinkConfig> search(String key, String url, String dateFrom, String dateTo, int number, int size, String orderBy, boolean desc);

}
