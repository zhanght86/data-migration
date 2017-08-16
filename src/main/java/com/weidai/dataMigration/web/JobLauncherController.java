/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.web;

import com.weidai.dataMigration.dal.ucenter.UserBaseDoMapper;
import com.weidai.dataMigration.util.UserMigrationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuqi 2017/8/9 0009.
 */
@RestController
public class JobLauncherController {
    private static final Logger logger = LoggerFactory.getLogger(JobLauncherController.class);
    
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Autowired
    UserBaseDoMapper userBaseDoMapper;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/run")
    public void runJob(@RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize, @RequestParam(name = "maxUid") Integer maxUid) throws Exception {
        UserMigrationHolder.PAGE_SIZE = pageSize;
        UserMigrationHolder.MAX_UID = maxUid;
        int count = userBaseDoMapper.count(maxUid);
        UserMigrationHolder.TOTAL_PAGE = (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        logger.info("total elements count: {}, total page : {}", count, UserMigrationHolder.TOTAL_PAGE);
        logger.info("dataMigrationJob is starting...");
        jobLauncher.run(job, new JobParameters());
    }
}
