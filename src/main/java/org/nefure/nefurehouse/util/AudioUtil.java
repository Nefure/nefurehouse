package org.nefure.nefurehouse.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.URLUtil;
import com.mpatric.mp3agic.*;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.dto.AudioInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author nefure
 * @date 2022/3/24 20:47
 */
public class AudioUtil {

    private static final Logger log = LoggerFactory.getLogger(AudioUtil.class);

    public static AudioInfoDTO getAudioInfo(String url) throws Exception {
        String query = new URL(url).getQuery();
        if(query != null){
            url = url.replace(query, URLUtil.encode(query));
        }

        //超过最大就直接放弃解析
        if(HttpUtil.getRemoteFileSize(url) > (1024*1024*HouseConstant.TEXT_MAX_FILE_SIZE_MB)){
            return AudioInfoDTO.buildDefaultAudioInfoDTO();
        }

        String fullFilePath = StringUtils.removeDuplicateSeparator(HouseConstant.TMP_FILE_PATH + HouseConstant.PATH_SEPARATOR + UUID.fastUUID());

        File file = new File(fullFilePath);
        FileUtil.mkParentDirs(file);
        cn.hutool.http.HttpUtil.downloadFile(url, file);
        AudioInfoDTO audioInfoDTO = parseAudioInfo(file);
        audioInfoDTO.setSrc(url);
        file.deleteOnExit();
        return audioInfoDTO;
    }

    private static AudioInfoDTO parseAudioInfo(File file) throws IOException, UnsupportedTagException {
        AudioInfoDTO audioInfoDTO = AudioInfoDTO.buildDefaultAudioInfoDTO();

        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(file);
        } catch (InvalidDataException e) {
            if (log.isDebugEnabled()) {
                log.debug("无法解析的音频文件.");
            }
        }

        if (mp3File == null) {
            return audioInfoDTO;
        }

        ID3v1 audioTag = null;

        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            byte[] albumImage = id3v2Tag.getAlbumImage();
            if (albumImage != null) {
                audioInfoDTO.setCover("data:" + id3v2Tag.getAlbumImageMimeType() + ";base64," + Base64.encode(albumImage));
            }
            audioTag = id3v2Tag;
        }

        if (audioTag != null) {
            audioInfoDTO.setTitle(audioTag.getTitle());
            audioInfoDTO.setArtist(audioTag.getArtist());
        }

        return audioInfoDTO;
    }
}
