package org.nefure.nefurehouse.config;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author nefure
 * @Description 准备应用环境
 * @CreateTime 2022年10月02日 12:28:00
 */
@Slf4j
@Component
public class HouseListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        try{
            boolean success = new File(HouseConstant.LOG).mkdirs();
            success = new File(HouseConstant.TMP_FILE_PATH).mkdirs();
        }catch (Exception e){
            log.warn(e.toString());
        }
    }
}
