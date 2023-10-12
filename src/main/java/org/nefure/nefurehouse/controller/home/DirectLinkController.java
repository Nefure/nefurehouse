package org.nefure.nefurehouse.controller.home;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.exception.NotEnabledDriveException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.DriverConfig;
import org.nefure.nefurehouse.model.enums.FileTypeEnum;
import org.nefure.nefurehouse.service.DriverConfigService;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.util.HttpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.HandlerMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * 经过直链重定向到目标文件
 * @author nefure
 * @date 2022/3/24 16:14
 */
@CrossOrigin
@Controller
public class DirectLinkController {

    @Resource
    private DriveContext driveContext;

    @Resource
    private DriverConfigService service;

    @GetMapping("/${nefurehouse.directLinkPrefix}/{driveId}/**")
    public String directLink(@PathVariable Long driveId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        //判断是否可用
        DriverConfig driverConfig = service.getDriverConfigById(driveId);
        if(!driverConfig.getEnable()){
            throw new NotEnabledDriveException();
        }
        //获取路径（除开ip/域名后的部分）
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        //拿到匹配规则（mapping 里的串）
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        //获取除开匹配部分的其余部分（这里是’**‘部分），即请求的文件路径
        String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);

        if (!filePath.isEmpty() && filePath.charAt(0) != HouseConstant.PATH_SEPARATOR_CHAR) {
            filePath = "/" + filePath;
        }

        AbstractBaseFileService fileService = driveContext.get(driveId);
        FileItemDTO fileItem = fileService.getFileItem(filePath);
        String url = fileItem.getUrl();
        if (StrUtil.equalsIgnoreCase(FileUtil.extName(fileItem.getName()), "m3u8")) {
            String textContent = HttpUtil.getTextContent(url);
            response.setContentType("application/vnd.apple.mpegurl;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(textContent);
            out.flush();
            out.close();
            return null;
        }

        int queryIndex = url.indexOf('?');

        if (queryIndex != -1) {
            String origin = url.substring(0, queryIndex);
            String queryString = url.substring(queryIndex + 1);

            url = URLUtil.encode(origin) + "?" + URLUtil.encode(queryString);
        } else {
            url = URLUtil.encode(url);
        }

        if (Objects.equals(fileItem.getType(), FileTypeEnum.FOLDER)) {
            return "redirect:" + fileItem.getUrl();
        } else {
            return "redirect:" + url;
        }
    }

}
