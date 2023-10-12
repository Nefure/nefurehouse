package org.nefure.nefurehouse.controller.home;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.context.DriveContext;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.base.AbstractBaseFileService;
import org.nefure.nefurehouse.util.AudioUtil;
import org.nefure.nefurehouse.util.HttpUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author nefure
 * @date 2022/3/24 20:38
 */
@Slf4j
@RestController
@RequestMapping("")
public class FileController {

    @Resource
    private DriveContext driveContext;

    /**
     * 获取普通的文本文件
     */
    @GetMapping("/common/content")
    public ResultData getContent(String url) {
        return ResultData.successData(HttpUtil.getTextContent(url));
    }

    /**
     * 获取支持的音频信息的摘要（封面、文件名、地址、长度等）
     *
     * @param url 文件url
     * @return 摘要信息
     */
    @GetMapping("/common/audio-info")
    public ResultData getAudioInfo(String url) throws Exception {
        return ResultData.successData(AudioUtil.getAudioInfo(url));
    }

    @PostMapping("/file/upload")
    public ResultData uploadFile(@RequestParam MultipartFile file, @RequestParam(defaultValue = "/") String path, @RequestParam long driverId) throws ExecutionException, InterruptedException {
        if (file.getSize() > HouseConstant.TEXT_MAX_FILE_SIZE_MB * HouseConstant.M) {
            throw new RuntimeException("请求错误，大于5MB的文件");
        }
        AbstractBaseFileService fileService = driveContext.get(driverId);
        if (fileService.uploadSimple(path, file).get()) {
            return ResultData.success();
        }
        return ResultData.error();
    }

    @PostMapping("/file/upload-parts")
    public ResultData uploadFileParts(@RequestParam MultipartFile chunk, @RequestParam long driverId, String hash, int index, Integer chunkCnt, long size) throws IOException, ExecutionException, InterruptedException {
        AbstractBaseFileService fileService = driveContext.get(driverId);
        return ResultData.successData(fileService.uploadParts(chunk, hash, index, chunkCnt, size).get());
    }

    @PostMapping("/file/commit-parts")
    public ResultData uploadFilePartsCommit(@RequestParam long driverId, String hash, @RequestParam(defaultValue = "/") String path, @RequestParam String name) throws ExecutionException, InterruptedException {
        AbstractBaseFileService fileService = driveContext.get(driverId);
        fileService.commitParts(path, hash, name).get();
        return ResultData.success();
    }

    /**
     * 下载指定文件
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
    public void downAttachment(@PathVariable("driveId") Long driveId, String type, final HttpServletRequest request, final HttpServletResponse response) throws ExecutionException, InterruptedException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();
        String filePath = apm.extractPathWithinPattern(bestMatchPattern, path);
        AbstractBaseFileService fileService = driveContext.get(driveId);
        fileService.download(request,response,type,filePath).get();
    }
}
