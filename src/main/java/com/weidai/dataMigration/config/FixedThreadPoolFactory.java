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

    public ExecutorService getThreadPool(int poolSize, String threadName) {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(poolSize),
                new NamedThreadFactory(threadName), new AbortPolicyWithReport());
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

    class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            String msg = String.format(
                    "Thread pool is EXHAUSTED!" + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                            + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                    "sql-execution-thread", e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                    e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
            logger.warn(msg);
            throw new RejectedExecutionException(msg);
        }
    }
}
