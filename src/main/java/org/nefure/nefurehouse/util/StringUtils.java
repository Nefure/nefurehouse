package org.nefure.nefurehouse.util;

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
}
