/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.service.UserMigrationService;
import com.weidai.dataMigration.util.UserMigrationHolder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

/**
 * @author wuqi 2017/8/1 0001.
 */
@Configuration
public class BatchConfig {

    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean().getObject();
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository){
        return new JobBuilderFactory(jobRepository);
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository){
        return new StepBuilderFactory(jobRepository, new ResourcelessTransactionManager());
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository){
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public DataMigrationItemReader<List<UserBaseDo>> dataMigrationItemReader(@Qualifier("ucenterSSF") SqlSessionFactory sqlSessionFactory) {
        DataMigrationItemReader<List<UserBaseDo>> itemReader = new DataMigrationItemReader<>();
        itemReader.setSqlSessionFactory(sqlSessionFactory);
        itemReader.setQueryId("com.weidai.dataMigration.dal.ucenter.UserBaseDoMapper.listByPage");
        itemReader.setPageSize(UserMigrationHolder.PAGE_SIZE);
        return itemReader;
    }

    @Bean
    public UserBaseItemProcessor userBaseItemProcessor(){
        return new UserBaseItemProcessor();
    }

    @Bean
    public DataMigrationCompletionListener listener(){
        return new DataMigrationCompletionListener();
    }
    
    @Bean
    public DataMigrationItemWriter<List<UserBaseDo>> dataMigrationItemWriter(UserMigrationService userMigrationService) {
        DataMigrationItemWriter<List<UserBaseDo>> itemWriter = new DataMigrationItemWriter<>();
        itemWriter.setMigrationService(userMigrationService);
        return itemWriter;
    }

    @Bean
    @Qualifier("step1")
    public Step step(StepBuilderFactory stepBuilderFactory, DataMigrationItemReader<List<UserBaseDo>> itemReader, UserBaseItemProcessor itemProcessor,
            DataMigrationItemWriter<List<UserBaseDo>> itemWriter) {
        return stepBuilderFactory.get("step1")
                .<List<UserBaseDo>, List<UserBaseDo>> chunk(1)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job userMigrationJob(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step, DataMigrationCompletionListener listener) {
        return jobBuilderFactory.get("dataMigrationJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
}
