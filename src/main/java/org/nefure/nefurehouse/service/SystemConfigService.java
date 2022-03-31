package org.nefure.nefurehouse.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.mapper.SystemConfigMapper;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.constant.SystemConfigConstant;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.entity.SystemConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author nefure
 * @date 2022/3/17 19:25
 */
@Slf4j
@Service
public class SystemConfigService {

    @Resource
    private HouseCache houseCache;

    private final Class<SystemConfigDTO> systemConfigClazz = SystemConfigDTO.class;

    @Resource
    private SystemConfigMapper systemConfigMapper;

    /**
     *
     * @return 是否初始化
     */
    public Boolean isInstalled() {
        return StrUtil.isNotEmpty(getSystemConfig().getUsername());
    }

    public SystemConfigDTO getSystemConfig(){
        SystemConfigDTO cacheConfig = houseCache.getConfig();
        if(cacheConfig != null){
            return cacheConfig;
        }

        SystemConfigDTO systemConfigDTO = getDbSystemConfig();
        if(systemConfigDTO == null){
            systemConfigDTO = getDefaultSystemConfig();
            updateSystemConfig(systemConfigDTO);
        }
        houseCache.updateSystemConfig(systemConfigDTO);
        return systemConfigDTO;
    }

    /**
     *从数据库加载当前用户的系统配置
     */
    private SystemConfigDTO getDbSystemConfig() {
        SystemConfigDTO systemConfigDTO = null;
        List<SystemConfig> systemConfigList = systemConfigMapper.findAll();

        if(systemConfigList != null && systemConfigList.size() > 0) {
            systemConfigDTO = new SystemConfigDTO();
            for (SystemConfig systemConfig : systemConfigList) {
                String key = systemConfig.getKey();

                try {
                    Field field = systemConfigClazz.getDeclaredField(key);
                    field.setAccessible(true);
                    String strVal = systemConfig.getValue();
                    Object convertVal = Convert.convert(field.getType(), strVal);
                    field.set(systemConfigDTO, convertVal);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("通过反射, 将字段 {} 注入 SystemConfigDTO 时出现异常:", key, e);
                }
            }
        }
        return systemConfigDTO;
    }

    private SystemConfigDTO getDefaultSystemConfig(){
        SystemConfigDTO systemConfigDTO;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File json = new ClassPathResource(HouseConstant.JSON_FILE).getFile();
            systemConfigDTO = objectMapper.readValue(json, SystemConfigDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取默认配置失败！");
        }
        return systemConfigDTO;
    }

    public void updateSystemConfig(SystemConfigDTO systemConfigDTO) {
        List<SystemConfig> systemConfigs = toSystemConfigs(systemConfigDTO);
        houseCache.removeSystemConfig();
        saveAll(systemConfigs);
    }

    private List<SystemConfig> toSystemConfigs(SystemConfigDTO systemConfigDTO) {
        List<SystemConfig> systemConfigList = new ArrayList<>();

        Field[] fields = systemConfigClazz.getDeclaredFields();
        for (Field field : fields) {
            String key = field.getName();
            SystemConfig systemConfig = systemConfigMapper.findByKey(key);
            if (systemConfig != null) {
                field.setAccessible(true);
                Object val = null;

                try {
                    val = field.get(systemConfigDTO);
                } catch (IllegalAccessException e) {
                    log.error("通过反射, 从 SystemConfigDTO 获取字段 {}  时出现异常:", key, e);
                }

                if (val != null) {
                    systemConfig.setValue(val.toString());
                    systemConfigList.add(systemConfig);
                }
            }
        }
        return systemConfigList;
    }

    /**
     * 保存配置项（有则更新，无则插入）
     * @param systemConfigs 需要保存的项
     */
    private void saveAll(List<SystemConfig> systemConfigs) {
        //排除空对象
        List<SystemConfig> nonNullList = systemConfigs.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> hasId = nonNullList.stream().map(SystemConfig::getId).filter(Objects::nonNull).collect(Collectors.toList());
        if(hasId.size() > 0){
            //提取出更新的项并更新
            Set<Integer> contains = systemConfigMapper.findByIds(hasId);
            List<SystemConfig> updates = nonNullList.stream().filter(systemConfig -> systemConfig.getId() != null && contains.contains(systemConfig.getId())).collect(Collectors.toList());
            updateAll(updates);

            //其他部分进行插入操作
            List<SystemConfig> inserts = nonNullList.stream().filter(systemConfig -> systemConfig.getId() == null || !contains.contains(systemConfig.getId())).collect(Collectors.toList());
            insertAll(inserts);
        }
        else{
            updateAll(nonNullList);
        }
    }

    /**
     * 批量插入
     * @param ls 插入的记录
     */
    public void insertAll(List<SystemConfig> ls){
        if(ls != null && ls.size() > 0){
            systemConfigMapper.saveAll(ls);
        }
    }

    /**
     * 通过id批量修改
     */
    public void updateAll(List<SystemConfig> ls){
        if(ls != null && ls.size() > 0){
            systemConfigMapper.updateAll(ls);
        }
    }

    public void updateUsernameAndPwd(String name, String pwd) {
        SystemConfig username = systemConfigMapper.findByKey(SystemConfigConstant.USERNAME);
        SystemConfig password = systemConfigMapper.findByKey(SystemConfigConstant.PASSWORD);
        username.setValue(name);
        password.setValue(pwd);
        systemConfigMapper.update(username,password);
    }

}
