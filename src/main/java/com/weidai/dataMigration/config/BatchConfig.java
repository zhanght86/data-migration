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
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

/**
 * @author wuqi 2017/8/1 0001.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserMigrationService userMigrationService;

    @Bean
    @Primary
    public DataSource nullableDataSource() {
        return null;
    }

    @Bean
    public Job userMigrationJob(JobRepository jobRepository, Step step) {
        return jobBuilderFactory.get("userMigrationJob").repository(jobRepository).start(step).build();
    }

    @Bean
    public MyBatisPagingItemReader<UserBaseDo> myBatisPagingItemReader(@Qualifier("ucenterSSF") SqlSessionFactory sqlSessionFactory) {
        MyBatisPagingItemReader<UserBaseDo> itemReader = new MyBatisPagingItemReader<>();
        itemReader.setSqlSessionFactory(sqlSessionFactory);
        itemReader.setQueryId("com.weidai.dataMigration.domain.UserBaseDo.listByPage");
        itemReader.setPageSize(10_000);
        itemReader.setMaxItemCount(4_000_000);
        return itemReader;
    }

    @Bean
    public Step step(MyBatisPagingItemReader<UserBaseDo> itemReader) {
        return stepBuilderFactory.get("step1").chunk(10_000).reader(itemReader).build();
    }
}
