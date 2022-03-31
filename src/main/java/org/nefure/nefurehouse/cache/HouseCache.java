package org.nefure.nefurehouse.cache;

import cn.hutool.cache.impl.CacheObj;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存类
 *
 * @author nefure
 * @date 2022/3/13 12:52
 */
@Slf4j
@Component
public class HouseCache {


    /**
     * 缓存过期时间
     */
    @Value("${nefurehouse.cache.timeout}")
    private long timeout;

    /**
     * 缓存自动刷新间隔
     */
    @Value("${nefurehouse.cache.auto-refresh.interval}")
    private long autoRefreshInterval;

    private SystemConfigDTO systemConfigCache;

    @Resource
    private DriverConfigService driverConfigService;

    /**
     * 文件缓存项
     * 每个驱动id对应应该缓存表，
     * 缓存表内，每个路径对应此路径下的文件表
     */
    private final Map<Integer, TimedClearCache<String, List<FileItemDTO>>> driveCache = new ConcurrentHashMap<>();

    public TimedClearCache<String, List<FileItemDTO>> getCacheByDriveId(Integer driveId) {
        TimedClearCache<String, List<FileItemDTO>> cache;
        if ((cache = driveCache.get(driveId)) != null) {
            return cache;
        }
        return initDriveCache(driveId);
    }

    private synchronized TimedClearCache<String, List<FileItemDTO>> initDriveCache(Integer driveId) {
        TimedClearCache<String, List<FileItemDTO>> cache;
        cache = driveCache.get(driveId);
        if (cache == null) {
            cache = new TimedClearCache<>(timeout * 1000);
            driveCache.put(driveId, cache);
        }
        return cache;
    }

    /**
     * 写入缓存
     *
     * @param driveId 驱动器 ID
     * @param key     文件夹路径
     * @param value   文件夹中列表
     */
    public synchronized void put(Integer driveId, String key, List<FileItemDTO> value) {
        getCacheByDriveId(driveId).put(key, value);
    }


    /**
     * 获取指定驱动器, 某个文件夹的名称
     *
     * @param   driveId
     *          驱动器 ID
     *
     * @param   key
     *          文件夹路径
     *
     * @return  驱动器中文件夹的内容
     */
    public List<FileItemDTO> get(Integer driveId, String key) {
        return getCacheByDriveId(driveId).get(key, false);
    }


    /**
     * 清空指定驱动器的缓存.
     *
     * @param   driveId
     *          驱动器 ID
     */
    public void clear(Integer driveId) {
        if (log.isDebugEnabled()) {
            log.debug("清空驱动器所有缓存, driveId: {}", driveId);
        }
        getCacheByDriveId(driveId).clear();
    }

    /**
     * 获取特定驱动的缓存命中数
     *
     * @param driveId 要查询的驱动id
     */
    public int getHitCount(Integer driveId) {
        return getCacheByDriveId(driveId).getHitCount();
    }

    /**
     * 获取所有缓存 key (文件夹名称)
     *
     * @return      所有缓存 key
     */
    public Set<String> keySet(Integer driveId) {
        Iterator<CacheObj<String, List<FileItemDTO>>> cacheObjIterator = getCacheByDriveId(driveId).cacheObjIterator();
        Set<String> keys = new HashSet<>();
        while (cacheObjIterator.hasNext()) {
            keys.add(cacheObjIterator.next().getKey());
        }
        return keys;
    }

    /**
     * 从缓存中删除指定驱动器的某个路径的缓存
     *
     * @param   driveId
     *          驱动器 ID
     *
     * @param   key
     *          文件夹路径
     */
    public void remove(Integer driveId, String key) {
        getCacheByDriveId(driveId).remove(key);
    }

    public SystemConfigDTO getConfig() {
        return systemConfigCache;
    }

    public void updateSystemConfig(SystemConfigDTO systemConfigDTO) {
        this.systemConfigCache = systemConfigDTO;
    }

    public void removeSystemConfig() {
        systemConfigCache = null;
    }

    /**
     * 开启缓存自动刷新
     *
     * @param   driveId
     *          驱动器 ID
     */
    public void startAutoCacheRefresh(Integer driveId) {
        if (log.isDebugEnabled()) {
            log.debug("开启缓存自动刷新 driveId: {}", driveId);
        }
        DriverConfig driveConfig = driverConfigService.getDriverConfigById(driveId);
        Boolean autoRefreshCache = driveConfig.getAutoRefreshCache();
        if (autoRefreshCache != null && autoRefreshCache) {
            TimedClearCache<String, List<FileItemDTO>> cache = driveCache.get(driveId);
            if (cache == null) {
                cache = new TimedClearCache<>(timeout * 1000);
                driveCache.put(driveId,cache);
            }
            cache.schedulePrune(autoRefreshInterval*1000);
        }
    }

    /**
     * 停止缓存自动刷新
     *
     * @param   driveId
     *          驱动器 ID
     */
    public void stopAutoCacheRefresh(Integer driveId) {
        if (log.isDebugEnabled()) {
            log.debug("停止缓存自动刷新 driveId: {}", driveId);
        }
        TimedClearCache<String , List<FileItemDTO>> cache = driveCache.get(driveId);
        if (cache != null) {
            cache.cancelPruneSchedule();
        }
    }

}
