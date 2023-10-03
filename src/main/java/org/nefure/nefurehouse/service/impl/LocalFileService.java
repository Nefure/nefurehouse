package org.nefure.nefurehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.exception.InitializeDriveException;
import org.nefure.nefurehouse.exception.NotExistFileException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.constant.StorageConfigConstant;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.service.StorageConfigService;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author nefure
 * @date 2022/3/21 15:32
 */
@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalFileService extends AbstractBaseFileService {

    @Resource
    private StorageConfigService storageConfigService;

    @Resource
    private SystemConfigService systemConfigService;

    @Getter
    private String filePath;

    @Override
    public void init(Long driveId) {
        this.driveId = driveId;
        Map<String, StorageConfig> stringStorageConfigMap = storageConfigService.getStringStorageConfigMap(driveId);
        mergeStrategyConfig(stringStorageConfigMap);
        filePath = stringStorageConfigMap.get(StorageConfigConstant.FILE_PATH_KEY).getValue();

        if(Objects.isNull(filePath)){
            log.debug("初始化存储策略 [{}] 失败: 参数不完整", getType().getDescription());
            isInitialized = false;
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new InitializeDriveException("文件路径: \"" + file.getAbsolutePath() + "\"不存在, 请检查是否填写正确.");
        } else {
            testConnection();
            isInitialized = true;
        }
    }

    @Override
    public List<FileItemDTO> fileList(String path) throws FileNotFoundException {
        if(StrUtil.startWith(path,"..")){
            return Collections.emptyList();
        }
        ArrayList<FileItemDTO> list = new ArrayList<>();
        String fullPath = StringUtils.removeDuplicateSeparator(filePath + HouseConstant.PATH_SEPARATOR + path);
        File root = new File(fullPath);
        if(!root.exists()){
            throw  new FileNotFoundException("文件不存在");
        }
        File[] files = root.listFiles();
        if(files != null){
            for (File file : files) {
                FileItemDTO itemDTO = new FileItemDTO(file,path);
                if(file.isFile()){
                    itemDTO.setUrl(getDownloadUrl(StringUtils.concatUrl(path, file.getName())));
                }
                list.add(itemDTO);
            }
        }
        return list;
    }

    @Override
    public FileItemDTO getFileItem(String path) {
        String fullPath = filePath + path;
        File file = new File(fullPath);
        if(!file.exists()){
            throw new NotExistFileException();
        }
        FileItemDTO fileItemDTO = new FileItemDTO(file,filePath);
        if(file.isFile()){
            fileItemDTO.setUrl(getDownloadUrl(path));
        }
        return fileItemDTO;
    }


    @Override
    public String getDownloadUrl(String path) {
        return StringUtils.removeDuplicateSeparator( systemConfigService.getSystemConfig().getDomain()+ "/file/" + driveId + HouseConstant.PATH_SEPARATOR + path);
    }

    @Override
    public List<StorageConfig> storageStrategyConfigList() {
        ArrayList<StorageConfig> storageConfigs = new ArrayList<>();
        storageConfigs.add(new StorageConfig("filePath","文件路径"));
        return storageConfigs;
    }

    @Override
    public StorageType getType() {
        return StorageType.LOCAL;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
