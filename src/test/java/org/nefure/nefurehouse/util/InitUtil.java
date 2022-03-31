package org.nefure.nefurehouse.util;

import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.service.SystemConfigService;

import java.lang.reflect.Field;

/**
 * @author nefure
 * @date 2022/3/18 18:33
 */
public class InitUtil {
    public static<T> void setField(T object, String fieldName, Object newField){
        try {
            Field houseCache = object.getClass().getDeclaredField(fieldName);
            houseCache.setAccessible(true);
            houseCache.set(object,newField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
