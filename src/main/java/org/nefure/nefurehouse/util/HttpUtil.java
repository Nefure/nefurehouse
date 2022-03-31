package org.nefure.nefurehouse.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.exception.PreviewException;
import org.nefure.nefurehouse.exception.TextParseException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author nefure
 */
@Slf4j
public class HttpUtil {

    /**
     * 获取 URL 对应的文件内容
     *
     * @param   url
     *          文件 URL
     * @return  文件 URL
     */
    public static String getTextContent(String url) {
        RestTemplate restTemplate = SpringContextHolder.getBean("restTemplate");

        long maxFileSize = 1024 * HouseConstant.TEXT_MAX_FILE_SIZE_KB;

        if (getRemoteFileSize(url) > maxFileSize) {
            throw new PreviewException("预览文件超出大小, 最大支持 " + FileUtil.readableFileSize(maxFileSize));
        }

        String result;
        try {
            result = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new TextParseException("文件解析异常, 请求 url = " + url + ", 异常信息为 = " + e.getMessage());
        }

        return result == null ? "" : result;
    }

    /**
     * 获取远程文件大小
     */
    public static Long getRemoteFileSize(String url) {
        long size = 0;
        URL urlObject;
        try {
            urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();
            size = conn.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

}
