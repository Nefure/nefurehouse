package org.nefure.nefurehouse;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nefure.nefurehouse.cache.HouseCache;
import org.nefure.nefurehouse.model.dto.DriverConfigDTO;
import org.nefure.nefurehouse.model.dto.StorageStrategyConfig;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.model.support.Page;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.nefure.nefurehouse.util.InitUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * @author nefure
 * @date 2022/3/18 18:19
 */
public class SystemConfigDTOServiceTest {


    private static SystemConfigService service = new SystemConfigService();


    static {
        InitUtil.setField(service,"houseCache",new HouseCache());
    }



}
