package org.nefure.nefurehouse.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nefure.nefurehouse.NefureHouseApplication;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.SystemConfig;
import org.nefure.nefurehouse.service.ShortLinkService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author nefure
 * @date 2022/3/22 19:15
 */
@SpringBootTest(classes = {NefureHouseApplication.class})
@RunWith(SpringRunner.class)
public class SystemConfigTest {

    @Resource
    SystemConfigMapper mapper;

    @Resource
    ShortLinkService service;

    @Resource
    DriverConfigMapper driverConfigMapper;

    @Test
    public void insertTest(){
        ArrayList<SystemConfig> systemConfigs = new ArrayList<>();
        systemConfigs.add(new SystemConfig());
        mapper.saveAll(systemConfigs);
    }

    @Test
    public void findTest(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(5);
        arrayList.add(2);
        System.out.println(mapper.findByIds(arrayList));
        System.out.println(mapper.getById(1));
    }

    @Test
    public void updateTest(){
        ArrayList<SystemConfig> systemConfigs = new ArrayList<>();
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setId(1);
        systemConfig.setKey("siteName");
        systemConfig.setValue("0.0");
        systemConfig.setRemark("站点名称");
        systemConfigs.add(systemConfig);
        systemConfigs.add(null);
        systemConfig = new SystemConfig();
        systemConfig.setId(6);
        systemConfig.setKey("username");
        systemConfig.setValue("nefure");
        systemConfig.setRemark("管理员账号");
        systemConfigs.add(systemConfig);
        systemConfigs.add(null);
        System.out.println(systemConfigs);
        System.out.println(mapper.updateAll(systemConfigs));
    }

    @Test
    public void testSearch(){
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setType("LOCAL");
        driverConfig.setEnable(true);
        driverConfig.setEnable(true);
        driverConfigMapper.save(driverConfig);
        System.out.println(driverConfig.getId());
    }
}
