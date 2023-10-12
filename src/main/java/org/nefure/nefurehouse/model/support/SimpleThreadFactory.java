package org.nefure.nefurehouse.model.support;

import lombok.Getter;
import lombok.NonNull;
import org.nefure.nefurehouse.util.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nefure
 * @date 2022/10/2 18:56
 */
public class SimpleThreadFactory implements ThreadFactory {

    @Getter
    private final String prefix;

    private boolean daemon;

    private final AtomicInteger index = new AtomicInteger(-1);

    public SimpleThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public SimpleThreadFactory(String prefix, boolean daemon){
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r,prefix + '-' + StringUtils.fillLeft(index.incrementAndGet(), '0',10)+ '_' + (daemon?'D':'U'));
        thread.setDaemon(daemon);
        return thread;
    }
}
