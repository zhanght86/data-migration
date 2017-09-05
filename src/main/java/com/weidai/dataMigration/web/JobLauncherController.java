/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.web;

import com.weidai.dataMigration.config.FixedThreadPoolFactory;
import com.weidai.dataMigration.config.UserBaseItemProcessor;
import com.weidai.dataMigration.dal.ucenter.UserBaseDoMapper;
import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.service.UserMigrationService;
import com.weidai.dataMigration.util.UserMigrationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    UserMigrationService userMigrationService;

    @GetMapping("/ping")
    public String ping() {
        return "pong!!!";
    }

    @GetMapping("/run")
    public void runJob(@RequestParam(name = "pageSize", required = false, defaultValue = "5000") Integer pageSize,
            @RequestParam("maxUid") Integer maxUid) throws Exception {
        UserMigrationHolder.PAGE_SIZE = pageSize;
        UserMigrationHolder.MAX_UID = maxUid;
        int count = userBaseDoMapper.count();
        UserMigrationHolder.TOTAL_PAGE = (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        logger.info("total elements count: {}, total page : {}", count, UserMigrationHolder.TOTAL_PAGE);
        logger.info("dataMigrationJob is starting...");
        jobLauncher.run(job, new JobParameters());
    }

    @GetMapping("/{startMobile}/{endMobile}")
    public String fixError(@PathVariable("startMobile") String startMobile, @PathVariable("endMobile") String endMobile,
            @RequestParam(value = "maxUid", required = false) Integer maxUid, @RequestParam("type") String type,
            @RequestParam(name = "startUserId", required = false) Integer startUserId) throws InterruptedException {
        if (UserMigrationService.executorService.isShutdown()) {
            UserMigrationService.executorService = FixedThreadPoolFactory.getInstance().getThreadPool(1, 7, "batch-insert-thread");
        }
        List<UserBaseDo> list = userBaseDoMapper.selectBetween(startMobile, endMobile, maxUid);
        logger.info("find {} items between {} and {}", list == null ? 0 : list.size(), startMobile, endMobile);
        if (list != null && !list.isEmpty()) {
            List<UserBaseDo> results = new ArrayList<>(list.size());
            int invalidCount = 0;
            for (UserBaseDo userBaseDo : list) {
                if (StringUtils.hasText(userBaseDo.getMobile()) && UserBaseItemProcessor.isValid(userBaseDo)
                        && UserBaseItemProcessor.isNotEmployee(userBaseDo)) {
                    results.add(userBaseDo);
                } else {
                    invalidCount++;
                }
            }
            if (!results.isEmpty()) {
                List<List<UserBaseDo>> wrapperList = new ArrayList<>(1);
                wrapperList.add(results);
                if (startUserId != null) {
                    UserMigrationHolder.initId(startUserId);
                }
                userMigrationService.migrate(wrapperList, type);
                // 等待所有已提交的任务完成
                UserMigrationService.executorService.shutdown();
                while (!UserMigrationService.executorService.awaitTermination(2, TimeUnit.SECONDS))
                    ;
                logger.info("All task has completed, invalid count: {}", invalidCount);
            }
        }
        return "Complete!";
    }

    @GetMapping("/reroll/{mobile}")
    public String reroll(@PathVariable("mobile") String mobile, @RequestParam(value = "startUserId", required = false) Integer startUserId)
            throws InterruptedException {
        userMigrationService.clearByMobile(mobile);
        fixError(mobile, mobile, null, UserMigrationHolder.DEFAULT_TYPE, startUserId);
        return "Success!";
    }
}
