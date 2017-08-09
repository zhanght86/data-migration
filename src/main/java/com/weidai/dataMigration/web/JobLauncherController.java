/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuqi 2017/8/9 0009.
 */
@RestController
public class JobLauncherController {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

    @GetMapping("/run")
    public void runJob() throws Exception{
        jobLauncher.run(job, new JobParameters());
    }
}
