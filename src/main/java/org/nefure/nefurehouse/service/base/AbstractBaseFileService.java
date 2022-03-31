package org.nefure.nefurehouse.service.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.exception.InitializeDriveException;
import org.nefure.nefurehouse.exception.PasswordVerifyException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.StorageType;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.model.support.VerifyResult;
import org.nefure.nefurehouse.service.FilterConfigService;
import org.nefure.nefurehouse.util.HttpUtil;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nefure
 * @date 2022/3/17 21:05
 */
@Slf4j
public abstract class AbstractBaseFileService implements BaseFileService {

    protected Integer driveId;

    protected boolean isInitialized;

    /**
     * 下载链接过期时间
     */
    long timeOut;

    /**
     * 基路径
     */
    String basePath;

    /**
     * 获取所有文件
     *
     * @param path 根目录
     * @return 根目录文件
     * @exception Exception 文件获取异常
     */
    @Override
    public abstract List<FileItemDTO> fileList(String path) throws Exception;

    /**
     * 初始化驱动对应的服务
     *
     * @param driveId 驱动id
     */
    public abstract void init(Integer driveId);

    /**
     * 测试是否连接成功, 会尝试取调用获取根路径的文件, 如果没有抛出异常, 则认为连接成功, 某些存储策略需要复写此方法.
     */
    protected void testConnection() {
        try {
            fileList("/");
        } catch (Exception e) {
            throw new InitializeDriveException("初始化异常, 错误信息为: " + e.getMessage(), e);
        }
    }

    public boolean getInstalled() {
        return isInitialized;
    }

    /**
     * 获取当前实现类的存储策略类型
     *
     * @return 存储策略类型枚举对象
     */
    public abstract StorageType getType();


    /**
     * 获取初始化当前存储策略, 所需要的参数信息 (用于表单填写)
     *
     * @return 初始化所需的参数列表
     */
    public abstract List<StorageConfig> storageStrategyConfigList();

    public void filterFileList(List<FileItemDTO> items, Integer driveId, FilterConfigService filterConfigService) {
        if (items == null) {
            return;
        }
        items.removeIf(item -> HouseConstant.FILE_NAME_PASSWORD.equals(item.getName())
                || HouseConstant.FILE_NAME_README.equals(item.getName())
                || filterConfigService.filterResultIsHidden(driveId, StringUtils.concatUrl(item.getPath(), item.getName())));
    }

    /**
     * 校验密码
     *
     * @param fileItemList  文件列表
     * @param path          请求路径
     * @param inputPassword 用户输入的密码
     * @return 是否校验通过
     */
    public VerifyResult verifyPassword(List<FileItemDTO> fileItemList, String path, String inputPassword) {
        for (FileItemDTO fileItemDTO : fileItemList) {
            if (HouseConstant.FILE_NAME_PASSWORD.equals(fileItemDTO.getName())) {
                String expectedPasswordContent;
                try {
                    expectedPasswordContent = HttpUtil.getTextContent(fileItemDTO.getUrl());
                } catch (HttpClientErrorException httpClientErrorException) {
                    log.trace("尝试重新获取密码文件缓存中链接后仍失败, driveId: {}, path: {}, inputPassword: {}, passwordFile:{} ",
                            driveId, path, inputPassword, JSON.toJSONString(fileItemDTO), httpClientErrorException);
                    try {
                        String pwdFileFullPath = StringUtils.removeDuplicateSeparator(fileItemDTO.getPath() + HouseConstant.PATH_SEPARATOR + fileItemDTO.getName());
                        FileItemDTO pwdFileItem = getFileItem(pwdFileFullPath);
                        expectedPasswordContent = HttpUtil.getTextContent(pwdFileItem.getUrl());
                    } catch (Exception e) {
                        throw new PasswordVerifyException("此文件夹为加密文件夹, 但密码检查异常, 请联系管理员检查密码设置", e);
                    }
                } catch (Exception e) {
                    throw new PasswordVerifyException("此文件夹为加密文件夹, 但密码检查异常, 请联系管理员检查密码设置", e);
                }

                if (matchPassword(expectedPasswordContent, inputPassword)) {
                    break;
                }

                if (StrUtil.isEmpty(inputPassword)) {
                    return VerifyResult.fail("此文件夹需要密码.", ResultData.REQUIRED_PASSWORD);
                }
                return VerifyResult.fail("密码错误.", ResultData.INVALID_PASSWORD);
            }
        }

        return VerifyResult.success();
    }

    /**
     * 获取单个文件信息
     *
     * @param path 文件路径
     * @return 单个文件的内容.
     */
    public abstract FileItemDTO getFileItem(String path);


    /**
     * 校验两个密码是否相同, 忽略空白字符
     *
     * @param expectedPasswordContent 预期密码
     * @param password                实际输入密码
     * @return 是否匹配
     */
    private boolean matchPassword(String expectedPasswordContent, String password) {
        if (Objects.equals(expectedPasswordContent, password)) {
            return true;
        }

        if (expectedPasswordContent == null) {
            return false;
        }

        if (password == null) {
            return false;
        }

        expectedPasswordContent = expectedPasswordContent.replace("\n", "").trim();
        password = password.replace("\n", "").trim();
        return Objects.equals(expectedPasswordContent, password);
    }

    /**
     * 合并数据库查询到的驱动器参数和驱动器本身支持的参数列表, 防止获取新增参数字段时出现空指针异常
     *
     * @param dbStorageConfigList 数据库查询到的存储列表
     */
    public void mergeStrategyConfig(Map<String, StorageConfig> dbStorageConfigList) {
        // 获取驱动器支持的参数列表
        List<StorageConfig> storageConfigs = this.storageStrategyConfigList();

        // 比对数据库已存储的参数列表和驱动器支持的参数列表, 找出新增的支持项
        Set<String> dbConfigKeySet = dbStorageConfigList.keySet();
        Set<String> allKeySet = storageConfigs.stream().map(StorageConfig::getKey).collect(Collectors.toSet());

        allKeySet.removeAll(dbConfigKeySet);

        // 对于新增的参数, put 到数据库查询的 Map 中, 防止程序获取时出现 NPE.
        for (String key : allKeySet) {
            StorageConfig storageConfig = new StorageConfig();
            storageConfig.setValue("");
            dbStorageConfigList.put(key, storageConfig);
        }
    }

    public Integer getDriveId() {
        return driveId;
    }

    public void setDriveId(Integer driveId) {
        this.driveId = driveId;
    }

    public String getReadme(List<FileItemDTO> items){
        if (!Objects.equals(getType(), StorageType.FTP)) {
            for (FileItemDTO item : items) {
                if(Objects.equals(item.getName(),HouseConstant.FILE_NAME_README)){
                    return HttpUtil.getTextContent(item.getUrl());
                }
            }
        }
        return null;
    }
}
