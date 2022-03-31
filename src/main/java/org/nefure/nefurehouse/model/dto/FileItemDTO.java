package org.nefure.nefurehouse.model.dto;

import lombok.Data;
import org.nefure.nefurehouse.model.enums.FileTypeEnum;

import java.io.File;
import java.util.Date;

/**
 * @author nefure
 * @date 2022/3/18 21:19
 */
@Data
public class FileItemDTO {

    private String name;
    private Date time;
    private Long size;
    private FileTypeEnum type;
    private String path;
    private String url;

    public FileItemDTO(){}

    public FileItemDTO(File file,String filePath){
        this.path = filePath;
        this.size = file.length();
        this.name = file.getName();
        this.time = new Date(file.lastModified());
        this.type = file.isDirectory()?FileTypeEnum.FOLDER:FileTypeEnum.FILE;
    }

}
