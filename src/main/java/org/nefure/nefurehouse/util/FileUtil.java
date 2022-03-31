package org.nefure.nefurehouse.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.catalina.connector.ClientAbortException;
import org.nefure.nefurehouse.exception.NotExistFileException;
import org.nefure.nefurehouse.model.constant.LocalFileResponseTypeConstant;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * 文件传输工具类
 * @author nefure
 * @date 2022/3/26 13:50
 */
public class FileUtil {
    /**
     * 返回文件给 response，支持断点续传和多线程下载 (动态变化的文件不支持)
     * @param request       请求对象
     * @param response      响应对象
     * @param file          下载的文件
     * @param fileName      下载的文件名，为空则默认读取文件名称
     */
    public static void export(HttpServletRequest request, HttpServletResponse response, File file, String fileName, String type) {
        //检查参数
        checkFileIsExist(file);
        fileName = StringUtils.isEmptyOrNull(fileName)?file.getName():fileName;

        //获取请求头的range参数（用于判断是否是断点重传，如果是，通过此参数获取请求的文件范围）
        String range = request.getHeader(HttpHeaders.RANGE);
        //初始化起止范围
        long startByte = 0;
        long endByte = file.length() -1;
        if(range != null && range.contains("bytes=") && range.contains("-")){
            //存在有效的range字段，说明是范围传输，按http规范，需要设置206响应码
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            //根据range的三种格式来进行解析
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                if(ranges.length == 1){
                    //格式一：345-
                    startByte = Long.parseLong(ranges[0]);
                }
                else if(ranges.length == 2){
                    //格式二：-345,三：12-345
                    if(!ranges[0].isBlank()){
                        startByte = Long.parseLong(ranges[0]);
                    }
                    endByte = Long.parseLong(ranges[1]);
                }
                //其他情况视为传送全部
            } catch (NumberFormatException e) {
                // 传参不规范，则直接返回所有内容
                startByte = 0;
                endByte = file.length() - 1;
            }
        }
        else{
            // 没有 ranges 即全部一次性传输，需要用 200 状态码，这一行应该可以省掉，因为默认返回是 200 状态码
            response.setStatus(HttpServletResponse.SC_OK);
        }
        long contentLen = endByte - startByte +1;
        String contentType = request.getServletContext().getMimeType(fileName);
        String form = LocalFileResponseTypeConstant.INLINE;
        if (Objects.equals(type, LocalFileResponseTypeConstant.DOWNLOAD) || StrUtil.isEmpty(contentType)) {
            contentType = "multipart/form-data";
            form = LocalFileResponseTypeConstant.ATTACHMENT;
        }
        //告诉浏览器，这边支持字节为单位的断点续传
        response.setHeader(HttpHeaders.ACCEPT_RANGES,"bytes");
        response.setHeader(HttpHeaders.CONTENT_TYPE,contentType);
        //设置默认保存的文件名及文件的展现形式
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,form+";filename="+ URLUtil.encode(fileName));
        response.setHeader(HttpHeaders.CONTENT_LENGTH,String.valueOf(contentLen));
        //告诉客户端这边传送的数据范围及总大小
        response.setHeader(HttpHeaders.CONTENT_RANGE,"bytes "+startByte+"-"+endByte+"/"+file.length());
        write(response,file,startByte,contentLen);
    }

    private static void write(HttpServletResponse response, File file, long startByte, long contentLength){
        try (var randomAccessFile = new RandomAccessFile(file,"r");var bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())){
            byte[] buff = new byte[4096];
            //已传送数据大小
            long transmitted = 0;
            int len = 0;
            randomAccessFile.seek(startByte);
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                bufferedOutputStream.write(buff, 0, len);
                transmitted += len;
            }
            // 处理不足 buff.length 部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                bufferedOutputStream.write(buff, 0, len);
            }
            bufferedOutputStream.flush();
            response.flushBuffer();
        } catch (ClientAbortException ignored) {} catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkFileIsExist(File file){
        if(file == null || !file.exists()){
            throw new NotExistFileException();
        }
    }

    /**
     * 在单线程中直接传送整个文件
     * @param file 要传送的文件
     * @param fileName 文件名
     * @return 响应实体
     */
    public static ResponseEntity<?> exportSingleThread(File file, String fileName) {
        if(!file.exists()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 FILE NOT FOUND");
        }
        //以八位字节流传送
        MediaType octetStream = MediaType.APPLICATION_OCTET_STREAM;
        HttpHeaders headers = new HttpHeaders();
        //告知客户端每次需要重新获取最新的
        headers.add(HttpHeaders.CACHE_CONTROL,"no-cache, no-store, must-revalidate");
        if(StringUtils.isEmptyOrNull(fileName)) {
            fileName = file.getName();
        }
        //声明响应数据应该以附件的形式接收
        headers.setContentDispositionFormData(LocalFileResponseTypeConstant.ATTACHMENT,URLUtil.encode(fileName));
        //兼容http1.0，确保告知了客户端不要缓存
        headers.add(HttpHeaders.PRAGMA,"no-cache");
        //直接把这个资源设置成已过期
        headers.add(HttpHeaders.EXPIRES,"0");
        //用此刻的时间戳作为指纹，告诉客户端要请求的资源已经发生变化
        headers.add(HttpHeaders.ETAG,String.valueOf(System.currentTimeMillis()));
        //直接把文件的数据流放入响应体
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(octetStream)
                .body(new FileSystemResource(file));
    }
}
