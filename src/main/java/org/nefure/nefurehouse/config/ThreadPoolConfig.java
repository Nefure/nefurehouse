package org.nefure.nefurehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author nefure
 * @Description 配置上传下载任务使用的线程池
 * @CreateTime 2023年10月10日 12:37:00
 */
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    @Value("${nefurehouse.downloadThreadPool.corePoolSize}")
    private int coreDownloadThreadCnt = 8;

    @Value("${nefurehouse.downloadThreadPool.maxPoolSize}")
    private int maxDownloadThreadCnt = 12;

    @Value("${nefurehouse.downloadThreadPool.queueCapacity}")
    private int maxDownloadTaskCnt = 256;

    @Value("${nefurehouse.downloadThreadPool.aliveSeconds}")
    private int downloadAliveSeconds = 60;

    @Value("${nefurehouse.uploadThreadPool.corePoolSize}")
    private int coreUploadThreadCnt = 8;

    @Value("${nefurehouse.uploadThreadPool.maxPoolSize}")
    private int maxUploadThreadCnt = 12;

    @Value("${nefurehouse.uploadThreadPool.queueCapacity}")
    private int maxUploadTaskCnt = 256;

    @Value("${nefurehouse.uploadThreadPool.aliveSeconds}")
    private int uploadAliveSeconds = 60;
    @Bean("downloadPool")
    public Executor downloadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreDownloadThreadCnt);
        executor.setMaxPoolSize(maxDownloadThreadCnt);
        executor.setQueueCapacity(maxDownloadTaskCnt);
        executor.setKeepAliveSeconds(downloadAliveSeconds);
        executor.setThreadNamePrefix("download-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean("uploadPool")
    public Executor uploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreUploadThreadCnt);
        executor.setMaxPoolSize(maxUploadThreadCnt);
        executor.setQueueCapacity(maxUploadTaskCnt);
        executor.setKeepAliveSeconds(uploadAliveSeconds);
        executor.setThreadNamePrefix("upload-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }
}
