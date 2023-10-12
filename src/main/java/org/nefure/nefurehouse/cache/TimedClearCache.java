package org.nefure.nefurehouse.cache;

import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;

import java.util.Map;

/**
 * 简单的定时自清理缓存
 * 不会自动更新
 * @author nefure
 * @date 2022/3/30 18:14
 */
public class TimedClearCache<K,V> extends TimedCache<K,V> {

    private static final long serialVersionUID = -1877232067299844508L;

    public TimedClearCache(long timeout) {
        super(timeout);
    }

    public TimedClearCache(long timeout, Map<K, CacheObj<K, V>> map) {
        super(timeout, map);
    }

}
