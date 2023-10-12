package org.nefure.nefurehouse.model.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author nefure
 * @date 2022/3/20 14:04
 */
@Configuration
public class HouseConstant {

    public static final String ROOT = System.getProperty("user.dir") + "/";
    public final static String USER_HOME = System.getProperty("user.home");
    public static final char PATH_SEPARATOR_CHAR = '/';
    public static final Long TEXT_MAX_FILE_SIZE_MB = 1L;
    public static Long AUDIO_MAX_FILE_SIZE_MB = 1L;
    public static String TMP_FILE_PATH = "/.house/tmp2/";
    public static String JSON_FILE;

    public static String REPO_FOLDER;

    public static int PIECE_SIZE_MB;

    public static final int M = 1024*1024;
    /**
     * 直链前缀名称
     */
    public static String DIRECT_LINK_PREFIX = "directlink";
    /**
     * 最大支持文本文件大小为 ? KB 的文件内容.
     */
    public static Long TEXT_MAX_FILE_SIZE_KB = 100L;

    public static String FILE_NAME_PASSWORD = "password.txt";

    public static String FILE_NAME_README = "readme.md";

    public static final String PATH_SEPARATOR = "/";

    public static String LOG = "log";

    public static String HOUSE = USER_HOME;

    /**
     * 是否自动删除上传产生的临时文件
     */
    public static boolean deleteFilesWhenClean;

    @Autowired(required = false)
    public void setJsonFile(@Value("${nefurehouse.json.systemConfig}") String jsonFile){
        JSON_FILE = jsonFile;
    }

    @Autowired(required = false)
    public void setHouse(@Value("${nefurehouse.house}") String house){
        HOUSE = ROOT + house;
    }

    @Autowired(required = false)
    public void setTmpFilePath(@Value("${nefurehouse.tmp.path}") String tmpFilePath) {
        TMP_FILE_PATH = ROOT + tmpFilePath;
    }


    @Autowired(required = false)
    public void setHeaderFileName(@Value("${nefurehouse.constant.readme}") String headerFileName) {
        FILE_NAME_README = headerFileName;
    }

    @Autowired(required = false)
    public void setPasswordFileName(@Value("${nefurehouse.constant.password}") String passwordFileName) {
        FILE_NAME_PASSWORD = passwordFileName;
    }

    @Autowired(required = false)
    public void setAudioMaxFileSizeMb(@Value("${nefurehouse.preview.audio.maxFileSizeMb}") Long maxFileSizeMb) {
        AUDIO_MAX_FILE_SIZE_MB = maxFileSizeMb;
    }

    @Autowired(required = false)
    public void setTextMaxFileSizeMb(@Value("${nefurehouse.preview.text.maxFileSizeKb}") Long maxFileSizeKb) {
        TEXT_MAX_FILE_SIZE_KB = maxFileSizeKb;
    }

    @Autowired(required = false)
    public void setDirectLinkPrefix(@Value("${nefurehouse.directLinkPrefix}") String directLinkPrefix) {
        DIRECT_LINK_PREFIX = directLinkPrefix;
    }

    @Autowired
    public void setLog(@Value("${nefurehouse.log.path}") String log){
        LOG = ROOT + log;
    }


    @Autowired
    public void setRepoFolder(@Value("${nefurehouse.repoFolder}") String path){
        REPO_FOLDER = path;
    }


    @Autowired
    public void setPieceSizeMb(@Value("${nefurehouse.tmp.pieceSizeMb}") int size){
        PIECE_SIZE_MB = size;
    }

    @Autowired
    public void setDeleteFilesWhenClean(@Value("${nefurehouse.tmp.autoDelete}") boolean auto){
        deleteFilesWhenClean = auto;
    }
}
