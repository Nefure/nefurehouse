package org.nefure.nefurehouse.util;

import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.model.support.SimpleThreadFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author nefure
 * @Description 文件分片上传的管理类
 * @CreateTime 2023年10月07日 00:17:00
 */
@Slf4j
public class FileCabinet {

    private static final HashMap<String, FileCabinet> FILE_CABINETS = new HashMap<>();

    /**
     * 限制表更新，保证每个对象从尝试获取到使用完成的线程安全
     */
    private static final ReentrantReadWriteLock MAPPER_LOCK = new ReentrantReadWriteLock();
    /**
     * 与表锁共同限制当前对象的销毁
     */
    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private static final ScheduledThreadPoolExecutor CLEANER = new ScheduledThreadPoolExecutor(1,new SimpleThreadFactory("FileCabinet_cleaner"));

    private static final String CONFIG_FILE_NAME = "loaded.nefure";

    public static boolean deleteFilesWhenClean = true;

    public static int PIECE_SIZE_MB;

    /**
     * 中转目录
     */
    public static String path;

    /**
     * 未初始化
     */
    private static final byte NEW = 0;
    /**
     * 可用
     */
    private static final byte READY = 1;
    /**
     * 已完成传输工作
     */
    private static final byte LOADED = 2;

    private byte status = NEW;

    private long lastUsed;


    private final long size;

    private final int[] loaded_log;

    private final int total;

    private final String configFileName;

    private final String storageFileName;

    private final ReentrantReadWriteLock editLock = new ReentrantReadWriteLock();

    static {
        CLEANER.scheduleAtFixedRate(FileCabinet::clean,5,5, TimeUnit.MINUTES);
    }

    /**
     * @param hash 文件哈希值
     */
    private FileCabinet(String hash, long size) {
        this.size = size;
        int tmp = PIECE_SIZE_MB * 1024 * 1024;
        total = (int)((size + tmp -1)/tmp);
        tmp *= 32;
        int len = (int) ((size + tmp - 1) / tmp);
        loaded_log = new int[len];
        configFileName = StringUtils.concatUrl(path, hash, CONFIG_FILE_NAME);
        storageFileName = StringUtils.concatUrl(path, hash, hash+".loading");
    }

    private void use() {
        LOCK.readLock().lock();
        lastUsed = System.currentTimeMillis();
        //尝试加载上一次保存的数据
        if (status == NEW) {
            loadLast();
        }
    }

    public void release(){
        lastUsed = System.currentTimeMillis();
        LOCK.readLock().unlock();
    }

    public static void clean(){
        //在读锁下获取表的快照
        int rt = 0;
        final int boundary = 300000;
        long cur = System.currentTimeMillis();
        ArrayList<Map.Entry<String, FileCabinet>> entries = new ArrayList<>();
        MAPPER_LOCK.readLock().lock();
        for (Map.Entry<String, FileCabinet> entry : FILE_CABINETS.entrySet()) {
            long last = entry.getValue().lastUsed;
            if (cur - last > boundary){
                entries.add(entry);
            }
        }
        MAPPER_LOCK.readLock().unlock();

        if (entries.isEmpty()){return;}

        MAPPER_LOCK.writeLock().lock();
        try {
            for (Map.Entry<String, FileCabinet> entry : entries) {
                FileCabinet fileCabinet = entry.getValue();
                if (cur - fileCabinet.lastUsed > boundary && fileCabinet.LOCK.writeLock().tryLock()){
                    try {
                        cur = System.currentTimeMillis();

                        rt++;
                        FILE_CABINETS.remove(entry.getKey());
                        if (deleteFilesWhenClean){
                            boolean deleted = new File(fileCabinet.storageFileName).delete();
                            log.info("try to delete temporary file `{}` {}",fileCabinet.storageFileName,deleted?"success":"failed");
                            deleted = new File(fileCabinet.configFileName).delete();
                            log.info("try to delete temporary file `{}` {}",fileCabinet.configFileName,deleted?"success":"failed");
                        }
                    }finally {
                        fileCabinet.LOCK.writeLock().unlock();
                    }
                }
            }
        }finally {
            if (MAPPER_LOCK.isWriteLockedByCurrentThread()) {
                MAPPER_LOCK.writeLock().unlock();
            }
        }
        log.info("清理上传任务，共清除：{}个。",rt);
    }


    /**
     * 从磁盘获取已接收的文件信息
     */
    private void loadLast() {
        //只在此写锁中才访问or修改记录文件
        editLock.writeLock().lock();
        try {
            if (status != NEW) {
                editLock.writeLock().unlock();
                return;
            }

            File configFile = new File(configFileName);
            if (!configFile.getParentFile().mkdirs()){
                log.debug("文件夹创建失败");
            }
            if ((!deleteFilesWhenClean) && configFile.exists()) {
                try (RandomAccessFile file = new RandomAccessFile(configFile, "rw")) {
                    FileChannel channel = file.getChannel();
                    long saved = channel.size();
                    int max = 50*1024;
                    if (saved > 0 && saved < max) {
                        if (LOADED == file.readByte()) {
                            status = LOADED;
                            editLock.writeLock().unlock();
                            return;
                        } else if (loaded_log.length == file.readInt()) {
                            status = READY;
                            int idx = 0;
                            while (file.getFilePointer() < saved) {
                                loaded_log[idx++] = file.readInt();
                            }
                        } else {
                            status = READY;
                            if (!configFile.delete()) {
                                log.debug("未能成功删除损坏的下载配置文件");
                            }
                        }
                    }
                } catch (IOException ignore) {}
            }else {
                status = READY;
            }

            try (RandomAccessFile file = new RandomAccessFile(new File(storageFileName), "rw")) {
                file.setLength(size);
            } catch (IOException ignored) {}
        }finally {
            if (editLock.isWriteLockedByCurrentThread()){
                editLock.writeLock().unlock();
            }
        }
    }

    public boolean upload(MultipartFile file, int idx, int chunkCnt, long size) {
        if (size != this.size || chunkCnt != total || idx < 0 || idx >= chunkCnt) {
            return false;
        }
        editLock.readLock().lock();
        if (status == LOADED || isLoaded(idx)){
            editLock.readLock().unlock();
            return true;
        }
        editLock.readLock().unlock();

        boolean rs = false;

        try (RandomAccessFile savedFile = new RandomAccessFile(new File(storageFileName),"rw")){
            FileChannel channel = savedFile.getChannel();
            long start = idx*PIECE_SIZE_MB*1024*1024L;
            long pieceSize = Math.min(PIECE_SIZE_MB*1024*1024L,size - start);

            FileLock lock = channel.lock(start, pieceSize, false);
            //不知是否必要，api没说有为空情况
            if(null != lock) {
                rs = channel.write(ByteBuffer.wrap(file.getBytes()),start) == pieceSize;
                lock.release();
            }
        }
        catch (OverlappingFileLockException e){
            return true;
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }

        if (rs) {
            editLock.writeLock().lock();
            if (setLoaded(idx)) {save();}
            editLock.writeLock().unlock();
        }

        return rs;
    }

    public boolean isComplete(){
        boolean complete;
        editLock.readLock().lock();
        complete = status == LOADED;
        editLock.readLock().unlock();
        return complete;
    }

    public void transformTo(String abstractPath) {
        FileUtil.copyTo(new File(storageFileName),new File(abstractPath));
    }

    /**
     *
     * @param idx 分片索引
     * @return 当前分片区已完成
     */
    private boolean setLoaded(int idx) {
        int seg = idx/32;
        int pos = idx%32;
        loaded_log[seg] = loaded_log[seg] | (1<<pos);
        if (loaded_log[seg] == -1){
            return true;
        }
        if (seg == loaded_log.length -1){
            return loaded_log[seg] == (-1>>>(31-((total -1)%32)));
        }
        return false;
    }

    private boolean isLoaded(int idx) {
        int seg = idx/32;
        int pos = idx%32;
        return 0 != (loaded_log[seg] & (1<<pos));
    }

    private boolean isLoaded(){
        for (int i = 0; i < loaded_log.length -1; i++){
            if (loaded_log[i] != -1){return false;}
        }
        return loaded_log[loaded_log.length -1] == -1>>>(31-((total -1)%32));
    }

    /**
     * 保存信息，需在调用前加锁保证线程安全
     */
    private void save() {
        File configFile = new File(configFileName);
        try (RandomAccessFile file = new RandomAccessFile(configFile, "rw")) {
            if (status == LOADED || isLoaded()) {
                status = LOADED;
                file.writeByte(LOADED);
            } else {
                file.writeByte(status);
                file.writeInt(loaded_log.length);
                for (int j : loaded_log) {
                    file.writeInt(j);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static FileCabinet get(@NonNull String hash, long size) {
        MAPPER_LOCK.readLock().lock();
        FileCabinet fileCabinet = FILE_CABINETS.get(hash);
        if (fileCabinet != null) {
            fileCabinet.use();
            MAPPER_LOCK.readLock().unlock();
            return fileCabinet;
        }

        //2.尝试创建
        MAPPER_LOCK.readLock().unlock();

        MAPPER_LOCK.writeLock().lock();
        try {
            //2.1再次检查
            if ((fileCabinet = FILE_CABINETS.get(hash)) != null) {
                //已被创建时锁降级后启用对象
                MAPPER_LOCK.readLock().lock();
                MAPPER_LOCK.writeLock().unlock();
                fileCabinet.use();
                MAPPER_LOCK.readLock().unlock();
                return fileCabinet;
            }
            //还未创建则创建入表，再锁降级进行初始化
            FILE_CABINETS.put(hash, fileCabinet = new FileCabinet(hash, size));
            MAPPER_LOCK.readLock().lock();
            fileCabinet.use();
            MAPPER_LOCK.readLock().unlock();
        }finally {
            if (MAPPER_LOCK.isWriteLockedByCurrentThread()) {
                MAPPER_LOCK.writeLock().unlock();
            }
        }
        return fileCabinet;
    }
}
