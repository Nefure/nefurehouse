package org.nefure.nefurehouse.controller.home;

import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.service.impl.LocalFileService;
import org.nefure.nefurehouse.util.FileUtil;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 获取本地存储的Controller
 * @author nefure
 * @date 2022/3/25 17:10
 */
@RestController
public class LocalController {

    @Resource
    private DriveContext driveContext;

    /**
     * 本地存储下载指定文件
     *
     * @param   driveId
     *          驱动器 ID
     * @param   type
     *          附件预览类型:
     *              download:下载
     *              default: 浏览器默认行为
     */
    @GetMapping("/file/{driveId}/**")
    @ResponseBody
    public void downAttachment(@PathVariable("driveId") Long driveId, String type, final HttpServletRequest request, final HttpServletResponse response) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();
        String filePath = apm.extractPathWithinPattern(bestMatchPattern, path);
        LocalFileService localService = (LocalFileService) driveContext.get(driveId);
        File file = new File(StringUtils.removeDuplicateSeparator(localService.getFilePath() + HouseConstant.PATH_SEPARATOR + filePath));
        FileUtil.export(request, response, file,null, type);
    }
}
