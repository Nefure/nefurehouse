package org.nefure.nefurehouse.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author nefure
 * @CreateTime 2023年10月11日 11:42:00
 */
public interface FileHashMapper {

    String TABLE_NAME = "file_uploaded";

    @Insert("insert into "+ TABLE_NAME +" (`hash`,`path`) values (#{hash},#{path});")
    void addFileHash(String hash,String path);

    /**
     * 通过文件md5值找到文件的位置
     * @param hash md5值
     * @return 文件位置
     */
    @Select("select `path` from "+TABLE_NAME + " where `hash` = #{hash};")
    List<String> find(String hash);

    @Select("SELECT `path` FROM "+ TABLE_NAME +" WHERE `hash` = #{hash} limit 0,1 for UPDATE ;")
    String findFirstWithLock(String hash);

    @Select("SELECT `path` FROM "+ TABLE_NAME +" WHERE `hash` = #{hash} limit 0,1;")
    String findFirst(String hash);

    @Select("select `hash` from "+ TABLE_NAME +" where `path` = #{path};")
    String findByPath(String path);

    @Select("select `hash` from "+ TABLE_NAME +" where `path` = #{path} for update;")
    String findByPathWithLock(String path);

    @Update("update "+ TABLE_NAME +" set `hash` = #{hash} where path=#{path};")
    void updateHash(String hash,String path);

    /**
     * 删除一项记录
     * @param path 路径
     * @param hash md5
     * @return 影响行数
     */
    @Delete("delete from "+ TABLE_NAME +" where `path` = #{path} and `hash` = #{hash};")
    int remove(String path,String hash);
}
