/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuqi 2017/8/14 0014.
 */
public class FixedThreadPoolFactory {
    private static final Logger logger = LoggerFactory.getLogger(FixedThreadPoolFactory.class);

    private FixedThreadPoolFactory() {
    }

    public static FixedThreadPoolFactory getInstance() {
        return new FixedThreadPoolFactory();
    }

    public ExecutorService getThreadPool(int corePoolSize, int maxPoolSize, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 2L, TimeUnit.HOURS, new SynchronousQueue<Runnable>(),
                new NamedThreadFactory(threadName), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNum = new AtomicInteger(1);

        private String threadName;

        public NamedThreadFactory(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(Runnable r) {
            String name = threadName + threadNum.getAndIncrement();
            Thread t = new Thread(r, name);
            t.setDaemon(true);
            return t;
        }
    }
}
