package org.nefure.nefurehouse.config;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.util.FileCabinet;
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
            FileCabinet.deleteFilesWhenClean = HouseConstant.deleteFilesWhenClean;
            FileCabinet.path = HouseConstant.REPO_FOLDER;
            FileCabinet.PIECE_SIZE_MB = HouseConstant.PIECE_SIZE_MB;
            boolean success = new File(HouseConstant.LOG).mkdirs();
            success = new File(HouseConstant.TMP_FILE_PATH).mkdirs();
        }catch (Exception e){
            log.warn(e.toString());
        }
    }
}
