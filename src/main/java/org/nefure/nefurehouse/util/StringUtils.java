package org.nefure.nefurehouse.util;

import cn.hutool.core.util.ObjectUtil;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.service.SystemConfigService;

/**
 * @author nefure
 * @date 2022/3/20 13:58
 */
public class StringUtils {


    public static final String DELIMITER_STR = "/";

    public static final String HTTP_PREFIX = "http://";

    public static final String HTTPS_PREFIX = "https://";

    public static char DELIMITER = '/';

    public static String concatUrl(String path, String name) {
        return removeDuplicateSeparator(DELIMITER + path + DELIMITER + name);
    }

    /**
     * 拼接 URL，并去除重复的分隔符 '/'，但不会影响 http:// 和 https:// 这种头部
     * @param strs      拼接的字符数组
     * @return          拼接结果
     */
    public static String concatUrl(String... strs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]);
            if (i != strs.length - 1) {
                sb.append(DELIMITER);
            }
        }
        return removeDuplicateSeparator(sb.toString());
    }

    public static String removeDuplicateSeparator(String path) {
        if (path == null || path.length() < 2) {
            return path;
        }

        StringBuilder sb = new StringBuilder();

        if (path.indexOf(HTTP_PREFIX) == 0) {
            sb.append(HTTP_PREFIX);
        } else if (path.indexOf(HTTPS_PREFIX) == 0) {
            sb.append(HTTPS_PREFIX);
        }

        for (int i = sb.length(); i < path.length() - 1; i++) {
            char current = path.charAt(i);
            char next = path.charAt(i + 1);
            if (!(current == DELIMITER && next == DELIMITER)) {
                sb.append(current);
            }
        }
        sb.append(path.charAt(path.length() - 1));
        return sb.toString();
    }

    public static boolean isEmptyOrNull(String fileName) {
        return fileName == null || fileName.isEmpty();
    }


    /**
     * 拼接文件直链生成 URL
     * @param driveId       驱动器 ID
     * @param fullPath      文件全路径
     * @return              生成结果
     */
    public static String generatorLink(Long driveId, String fullPath) {
        SystemConfigService systemConfigService = SpringContextHolder.getBean(SystemConfigService.class);
        String domain = systemConfigService.getDomain();
        return concatUrl(domain, HouseConstant.DIRECT_LINK_PREFIX, String.valueOf(driveId), fullPath);
    }

    /**
     * 获取 basePath + path 的全路径地址.
     * @return basePath + path 的全路径地址.
     */
    public static String getFullPath(String basePath, String path) {
        basePath = ObjectUtil.defaultIfNull(basePath, "");
        path = ObjectUtil.defaultIfNull(path, "");
        return StringUtils.removeDuplicateSeparator(basePath + HouseConstant.PATH_SEPARATOR + path);
    }

    /**
     * 去掉最前面的字符'/'："///is" -> "is"
     * null会被处理成空串处理
     */
    public static String removeFirstSeparators(String path) {
        StringBuilder sb = new StringBuilder();
        if(null != path&& !path.isEmpty()){
            int i = 0;
            while (i < path.length() && path.charAt(i)=='/'){
                i++;
            }
            sb.append(path,i,path.length());
        }
        return sb.toString();
    }


    /**
     * 将域名和路径组装成 URL, 主要用来处理分隔符 '/'
     * @param domain    域名
     * @param path      路径
     * @return          URL
     */
    public static String concatPath(String domain, String path) {
        if (path != null && path.length() > 1 && path.charAt(0) != DELIMITER) {
            path = DELIMITER + path;
        }

        if (domain != null && domain.charAt(domain.length() - 1) == DELIMITER) {
            domain = domain.substring(0, domain.length() - 2);
        }

        return domain + path;
    }


    public static String fillLeft(int num,char filler, int length){
        StringBuilder builder = new StringBuilder(Integer.toString(num)).reverse();
        while (builder.length() < length){
            builder.append(filler);
        }
        return builder.reverse().toString();
    }
}
