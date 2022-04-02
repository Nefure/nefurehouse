package org.nefure.nefurehouse.service.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.URLUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.nefure.nefurehouse.exception.NotExistFileException;
import org.nefure.nefurehouse.model.constant.HouseConstant;
import org.nefure.nefurehouse.model.constant.StorageConfigConstant;
import org.nefure.nefurehouse.model.dto.FileItemDTO;
import org.nefure.nefurehouse.model.entity.StorageConfig;
import org.nefure.nefurehouse.model.enums.FileTypeEnum;
import org.nefure.nefurehouse.service.StorageConfigService;
import org.nefure.nefurehouse.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;

/**
 * @author nefure
 * @date 2022/4/1 12:35
 */
public abstract class S3FileService extends AbstractBaseFileService {


    @Resource
    protected StorageConfigService storageConfigService;

    protected String bucketName;

    protected String domain;

    protected AmazonS3 s3Client;

    protected boolean isPrivate;

    /**
     * 获取当前存储的参数表
     * @return Map<参数名，参数信息>
     */
    private Map<String,StorageConfig> getStorageConfigMap(){
        Map<String, StorageConfig> map = storageConfigService.getStringStorageConfigMap(driveId);
        mergeStrategyConfig(map);
        return map;
    }


    @Override
    public String getDownloadUrl(String path) {
        basePath = basePath == null ? "" : basePath;
        String fullPath = StringUtils.removeFirstSeparators(StringUtils.removeDuplicateSeparator(basePath + HouseConstant.PATH_SEPARATOR + path));

        // 如果不是私有空间, 且指定了加速域名, 则直接返回下载地址.
        boolean isNullOrEmpty = domain != null && !"".equals(domain);
        if (BooleanUtil.isFalse(isPrivate) && !isNullOrEmpty) {
            return StringUtils.concatPath(domain, fullPath);
        }

        Date expirationDate = new Date(System.currentTimeMillis() + timeout * 1000);
        URL url = s3Client.generatePresignedUrl(bucketName, fullPath, expirationDate);

        String defaultUrl = url.toExternalForm();
        if (!isNullOrEmpty) {
            defaultUrl = URLUtil.complateUrl(domain, url.getFile());
        }
        return URLUtil.decode(defaultUrl);
    }

    @Override
    public List<FileItemDTO> fileList(String path) throws Exception {
        path = StringUtils.removeFirstSeparators(path);
        String fullPath = StringUtils.removeFirstSeparators(StringUtils.getFullPath(basePath, path));
        List<FileItemDTO> fileItemList = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest(bucketName, fullPath, "", "/", 1000));

        for (S3ObjectSummary s : objectListing.getObjectSummaries()) {
            FileItemDTO fileItemDTO = new FileItemDTO();
            if (s.getKey().equals(fullPath)) {
                continue;
            }
            fileItemDTO.setName(s.getKey().substring(fullPath.length()));
            fileItemDTO.setSize(s.getSize());
            fileItemDTO.setTime(s.getLastModified());
            fileItemDTO.setType(FileTypeEnum.FILE);
            fileItemDTO.setPath(path);

            String fullPathAndName = StringUtils.concatUrl(path, fileItemDTO.getName());
            String directLink = StringUtils.generatorLink(driveId, fullPathAndName);
            fileItemDTO.setUrl(directLink);

            fileItemList.add(fileItemDTO);
        }

        for (String commonPrefix : objectListing.getCommonPrefixes()) {
            FileItemDTO fileItemDTO = new FileItemDTO();
            if (Objects.equals(commonPrefix, "/")) {
                continue;
            }
            fileItemDTO.setName(commonPrefix.substring(fullPath.length(), commonPrefix.length() - 1));
            fileItemDTO.setType(FileTypeEnum.FOLDER);
            fileItemDTO.setPath(path);
            fileItemList.add(fileItemDTO);
        }

        return fileItemList;
    }

    /**
     * 获取单个文件时，把整个文件夹里的内容加入缓存（如果开启），
     * 但不会进行目录（'/'结尾）查找，如有需要，应直接调用listFile()方法
     * @param path 文件路径
     */
    @Override
    public FileItemDTO getFileItem(String path) {
        int end = path.lastIndexOf('/');
        List<FileItemDTO> items = null;

        //如果是目录，直接忽略
        if(end != path.length() -1) {
            try {
                String dirPath = path.substring(0, end + 1);
                items = fileList(dirPath);
            } catch (Exception ignored) {}
        }

        if(items != null){
            String fileName = path.substring(end +1);
            for (FileItemDTO item : items) {
                if(Objects.equals(fileName,item.getName())) {
                    item.setUrl(getDownloadUrl(path));
                    return item;
                }
            }
        }
        throw new NotExistFileException();
    }

    @Override
    public void init(Integer driveId) {
        this.driveId = driveId;
        Map<String, StorageConfig> storageConfigMap = getStorageConfigMap();
        String accessKey = storageConfigMap.get(StorageConfigConstant.ACCESS_KEY).getValue();
        String secretKey = storageConfigMap.get(StorageConfigConstant.SECRET_KEY).getValue();
        String endPoint = storageConfigMap.get(StorageConfigConstant.ENDPOINT_KEY).getValue();
        domain = storageConfigMap.get(StorageConfigConstant.DOMAIN_KEY).getValue();
        basePath = storageConfigMap.get(StorageConfigConstant.BASE_PATH).getValue();
        bucketName = storageConfigMap.get(StorageConfigConstant.BUCKET_NAME_KEY).getValue();
        isPrivate = Convert.toBool(storageConfigMap.get(StorageConfigConstant.IS_PRIVATE).getValue());

        if (Objects.isNull(accessKey) || Objects.isNull(secretKey) || Objects.isNull(endPoint) || Objects.isNull(bucketName)) {
            log.debug("初始化存储策略 [{}] 失败: 参数不完整", getType().getDescription());
            isInitialized = false;
        } else {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, getRegion())).build();

            testConnection();
            isInitialized = true;
        }
    }

    /**
     * 获取此存储策略的region
     * @return signingRegion
     */
    public abstract String getRegion();

    @Override
    public List<StorageConfig> storageStrategyConfigList() {
        return new ArrayList<>() {{
            add(new StorageConfig("accessKey", "AccessKey"));
            add(new StorageConfig("secretKey", "SecretKey"));
            add(new StorageConfig("bucketName", "Bucket 名称"));
            add(new StorageConfig("domain", "Bucket 域名 / CDN 加速域名"));
            add(new StorageConfig("endPoint", "区域"));
            add(new StorageConfig("basePath", "基路径"));
            add(new StorageConfig("isPrivate", "是否是私有空间"));
        }};
    }
}
