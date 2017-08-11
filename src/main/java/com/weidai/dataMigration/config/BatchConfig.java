/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.service.UserMigrationService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

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
    public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository, DataSourceTransactionManager transactionManager){
        return new StepBuilderFactory(jobRepository, transactionManager);
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository){
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public MyBatisPagingItemReader<UserBaseDo> myBatisPagingItemReader(@Qualifier("ucenterSSF") SqlSessionFactory sqlSessionFactory) {
        MyBatisPagingItemReader<UserBaseDo> itemReader = new MyBatisPagingItemReader<>();
        itemReader.setSqlSessionFactory(sqlSessionFactory);
        itemReader.setQueryId("com.weidai.dataMigration.dal.ucenter.UserBaseDoMapper.listByPage");
        itemReader.setPageSize(10_000);
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
    public DataMigrationItemWriter<UserBaseDo> dataMigrationItemWriter(UserMigrationService userMigrationService) {
        DataMigrationItemWriter<UserBaseDo> itemWriter = new DataMigrationItemWriter<>();
        itemWriter.setMigrationService(userMigrationService);
        return itemWriter;
    }

    @Bean
    @Qualifier("step1")
    public Step step(StepBuilderFactory stepBuilderFactory, MyBatisPagingItemReader<UserBaseDo> itemReader, UserBaseItemProcessor itemProcessor,
            DataMigrationItemWriter<UserBaseDo> itemWriter) {
        return stepBuilderFactory.get("step1")
                .<UserBaseDo, UserBaseDo> chunk(10_000)
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
