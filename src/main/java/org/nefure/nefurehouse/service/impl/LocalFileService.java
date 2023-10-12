package org.nefure.nefurehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.exception.InitializeDriveException;
import org.nefure.nefurehouse.exception.NotExistFileException;
import org.nefure.nefurehouse.mapper.FileHashMapper;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.constant.StorageConfigConstant;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.service.StorageConfigService;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.util.FileCabinet;
import org.nefure.nefurehouse.util.FileUtil;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author nefure
 * @date 2022/3/21 15:32
 */
@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalFileService extends AbstractBaseFileService {

    @Resource
    private FileHashMapper fileHashMapper;

    @Resource
    private StorageConfigService storageConfigService;

    @Resource
    private SystemConfigService systemConfigService;

    @Getter
    private String filePath;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public void init(Long driveId) {
        this.driveId = driveId;
        Map<String, StorageConfig> stringStorageConfigMap = storageConfigService.getStringStorageConfigMap(driveId);
        mergeStrategyConfig(stringStorageConfigMap);
        filePath = stringStorageConfigMap.get(StorageConfigConstant.FILE_PATH_KEY).getValue();

        if (Objects.isNull(filePath)) {
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
        final String prefixParent = "..";
        if (StrUtil.startWith(path, prefixParent)) {
            return Collections.emptyList();
        }
        ArrayList<FileItemDTO> list = new ArrayList<>();
        String fullPath = StringUtils.removeDuplicateSeparator(filePath + HouseConstant.PATH_SEPARATOR + path);
        File root = new File(fullPath);
        if (!root.exists()) {
            throw new FileNotFoundException("文件不存在");
        }
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                FileItemDTO itemDTO = new FileItemDTO(file, path);
                if (file.isFile()) {
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
        if (!file.exists()) {
            throw new NotExistFileException();
        }
        FileItemDTO fileItemDTO = new FileItemDTO(file, filePath);
        if (file.isFile()) {
            fileItemDTO.setUrl(getDownloadUrl(path));
        }
        return fileItemDTO;
    }


    @Override
    public String getDownloadUrl(String path) {
        return StringUtils.removeDuplicateSeparator(systemConfigService.getSystemConfig().getDomain() + "/file/" + driveId + HouseConstant.PATH_SEPARATOR + path);
    }

    @Override
    public List<StorageConfig> storageStrategyConfigList() {
        ArrayList<StorageConfig> storageConfigs = new ArrayList<>();
        storageConfigs.add(new StorageConfig("filePath", "文件路径"));
        return storageConfigs;
    }

    @Override
    public StorageType getType() {
        return StorageType.LOCAL;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    @Async("uploadPool")
    public CompletableFuture<Boolean> uploadSimple(String path, MultipartFile file) {
        String name = file.getOriginalFilename(), suffix;
        assert name != null;
        path = StringUtils.concatUrl(filePath, path, name.substring(0, name.lastIndexOf('.')));
        suffix = name.substring(name.lastIndexOf('.'));
        try {
            int idx = 0;
            File dest = new File(path + suffix);
            while (dest.exists()) {
                dest = new File(path + "(" + (++idx) + ")" + suffix);
            }
            file.transferTo(dest);
            return CompletableFuture.completedFuture(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async("uploadPool")
    public CompletableFuture<Boolean> uploadParts(MultipartFile file, String hash, int idx, int chunkCnt, long size) throws IOException {
        List<String> files = fileHashMapper.find(hash);
        if (files != null && !files.isEmpty()) {
            return CompletableFuture.completedFuture(true);
        }

        boolean uploadedAll;
        FileCabinet fileCabinet = FileCabinet.get(hash, size);
        try {
            boolean uploaded = fileCabinet.upload(file, idx, chunkCnt, size);
            if (!uploaded) {
                throw new RuntimeException("上传出错！");
            }
            uploadedAll = fileCabinet.isComplete();
        } finally {
            fileCabinet.release();
        }
        return CompletableFuture.completedFuture(uploadedAll);
    }

    @Override
    @Async("uploadPool")
    public CompletableFuture<Boolean> commitParts(String path, String hash, String name) {
        Boolean execute = transactionTemplate.execute(status -> {
            String file = fileHashMapper.findFirstWithLock(hash);
            if (!StringUtils.isEmptyOrNull(file)) {
                File target = new File(StringUtils.concatUrl(filePath, path, name));
                boolean success = false;
                try {
                    File src = new File(file);
                    if (src.exists()) {
                        FileUtil.copyTo(src, target);
                        success = true;
                    }
                } catch (Exception ignored) {}
                return success;
            } else {
                return null;
            }
        });
        if (execute != null) {
            return CompletableFuture.completedFuture(execute);
        }

        FileCabinet fileCabinet = FileCabinet.get(hash, 0);
        try {
            if (!fileCabinet.isComplete()) {
                throw new RuntimeException("请求错误！");
            }
            String target = StringUtils.concatUrl(filePath, path, name);
            fileCabinet.transformTo(target);
            if (hash.equals(FileUtil.getMd5(target))) {
                addFileHash(hash,target);
                return CompletableFuture.completedFuture(true);
            } else if (new File(target).delete()) {
                log.error("删除错误文件失败！{}", target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            fileCabinet.release();
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async("downloadPool")
    public CompletableFuture<Void> download(HttpServletRequest request, HttpServletResponse response, String type, String path) {
        File file = new File(StringUtils.removeDuplicateSeparator(filePath + HouseConstant.PATH_SEPARATOR + path));
        FileUtil.export(request, response, file,null, type);
        return null;
    }

    public void addFileHash(String hash,String path){
        transactionTemplate.executeWithoutResult(status -> {
            String oldHash = fileHashMapper.findByPathWithLock(path);
            if (StringUtils.isEmptyOrNull(oldHash)){
                fileHashMapper.addFileHash(hash,path);
            }else {
                fileHashMapper.updateHash(hash,path);
            }
        });
    }

}
