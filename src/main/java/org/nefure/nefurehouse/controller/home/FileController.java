package org.nefure.nefurehouse.controller.home;

import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.util.AudioUtil;
import org.nefure.nefurehouse.util.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nefure
 * @date 2022/3/24 20:38
 */
@RestController
@RequestMapping("/common")
public class FileController {

    /**
     * 获取普通的文本文件
     */
    @GetMapping("/content")
    public ResultData getContent(String url){
        return ResultData.successData(HttpUtil.getTextContent(url));
    }

    /**
     * 获取支持的音频信息的摘要（封面、文件名、地址、长度等）
     * @param url 文件url
     * @return 摘要信息
     */
    @GetMapping("/audio-info")
    public ResultData getAudioInfo(String url) throws Exception {
        return ResultData.successData(AudioUtil.getAudioInfo(url));
    }
}
