package org.nefure.nefurehouse.model.dto;

import lombok.Data;

/**
 * @author nefure
 * @date 2022/3/24 21:07
 */
@Data
public class AudioInfoDTO {

    private String title;

    private String artist;

    private String cover;

    private String src;

    public static AudioInfoDTO buildDefaultAudioInfoDTO() {
        AudioInfoDTO audioInfoDTO = new AudioInfoDTO();
        audioInfoDTO.setTitle("未知歌曲");
        audioInfoDTO.setArtist("未知");
        audioInfoDTO.setCover("http://c.jun6.net/audio.png");
        return audioInfoDTO;
    }
}
