package org.nefure.nefurehouse.controller;

import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.exception.NotEnabledDriveException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.dto.*;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.model.dto.SystemConfigDTO;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.model.support.VerifyResult;
import org.nefure.nefurehouse.service.*;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.util.FileComparator;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通用功能接口，包括获取启用的驱动、驱动下文件表、转短链接
 *
 * @author nefure
 * @date 2022/3/12 14:04
 */
@RequestMapping("/api")
@RestController
@CrossOrigin
public class ApiController {

    @Value("${nefurehouse.debug}")
    private Boolean debug;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private DriverConfigService driverConfigService;

    @Resource
    private FilterConfigService filterConfigService;

    @Resource
    private ShortLinkService shortLinkService;

    @Resource
    private DriveContext driverContext;

    /**
     * 获取所有可用的驱动列表
     *
     * @return 包含是否初始化信息 的列表
     */
    @GetMapping("/drive/list")
    public ResultData getDriveList() {
        return ResultData.successData(new DriveListDTO(driverConfigService.getOnlyEnable(), systemConfigService.isInstalled()));
    }

    /**
     * 根据 驱动id 获取 根目录
     *
     * @param driveId 驱动id
     * @return 目录信息
     */
    @GetMapping("/list/{driveId}")
    public ResultData getDriveListById(@PathVariable Long driveId,
                                       @RequestParam(required = false) String password,
                                       @RequestParam(required = false,defaultValue = "/") String path,
                                       @RequestParam(required = false,defaultValue = "asc") String orderDirection,
                                       @RequestParam(required = false) String orderBy) throws Exception {
        AbstractBaseFileService fileService = driverContext.get(driveId);
        List<FileItemDTO> items = fileService.fileList(StringUtils.removeDuplicateSeparator(HouseConstant.PATH_SEPARATOR + path + HouseConstant.PATH_SEPARATOR));

        VerifyResult result = fileService.verifyPassword(items, path, password);
        if(!result.isPassed()){
            return ResultData.error(result.getMsg(),result.getCode());
        }

        //过滤表达式要隐藏的的信息
        String readme = fileService.getReadme(items);
        fileService.filterFileList(items,driveId,filterConfigService);

        // 按照自然排序
        items.sort(new FileComparator(orderBy, orderDirection));

        //获取参数信息
        SystemFrontConfigDTO systemConfig = new SystemFrontConfigDTO(systemConfigService.getSystemConfig());
        DriverConfig driverConfig = driverConfigService.getDriverConfigById(driveId);
        Boolean enable = driverConfig.getEnable();
        if(!enable){
            throw new NotEnabledDriveException();
        }
        systemConfig.setSearchEnable(driverConfig.getSearchEnable());

        systemConfig.setDebugMode(debug);
        systemConfig.setDefaultSwitchToImgMode(driverConfig.getDefaultSwitchToImgMode());
        systemConfig.setDirectLinkPrefix(HouseConstant.DIRECT_LINK_PREFIX);

        systemConfig.setReadme(readme);
        return ResultData.successData(new FileListDTO(items,systemConfig));
    }

    /**
     * 获取短链接
     * @param driveId 驱动id
     * @param path    路径
     */
    @GetMapping("/short-link")
    public ResultData shortLink(String driveId, String path){
        SystemConfigDTO systemConfigDTO = systemConfigService.getSystemConfig();
        String domain = systemConfigDTO.getDomain();
        //拼接链接
        String fullPath = StringUtils.concatUrl(StringUtils.DELIMITER_STR, HouseConstant.DIRECT_LINK_PREFIX, driveId, path);
        ShortLinkConfig shortLinkConfig = shortLinkService.getByUrl(fullPath);

        if(shortLinkConfig == null){
            shortLinkConfig = shortLinkService.createShortLink(fullPath);
        }

        String shortLink = StringUtils.removeDuplicateSeparator(domain + "/s/" + shortLinkConfig.getKey());
        return ResultData.successData(shortLink);
    }
}
