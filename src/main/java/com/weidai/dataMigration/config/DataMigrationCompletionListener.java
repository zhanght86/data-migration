/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.service.UserMigrationService;
import com.weidai.dataMigration.util.UserMigrationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.util.concurrent.TimeUnit;

/**
 * @author wuqi 2017/8/10 0010.
 */
public class DataMigrationCompletionListener extends JobExecutionListenerSupport {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationCompletionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            try {
                // 等待所有已提交的任务完成
                UserMigrationService.executorService.shutdown();
                while(!UserMigrationService.executorService.awaitTermination(2, TimeUnit.SECONDS));
                logger.info("All task has completed!");
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            logger.info("!!! JOB FINISHED! INVALID_COUNT: {}", UserMigrationHolder.INVALID_COUNT.get());
        }
    }
}
