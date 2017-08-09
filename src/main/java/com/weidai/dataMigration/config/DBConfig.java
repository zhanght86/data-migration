/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author wuqi 2017/7/31 0031.
 */
@Configuration
public class DBConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.druid.ucenter")
    @Qualifier("ucenterDS")
    public DataSource ucenterDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("ucenterSSF")
    public SqlSessionFactory ucenterSqlSessionFactory(@Qualifier("ucenterDS") DataSource ucenterDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:configuration.xml"));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/ucenter/*.xml"));
        sqlSessionFactoryBean.setDataSource(ucenterDataSource);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    @Qualifier("ucenterSST")
    public SqlSessionTemplate ucenterSqlSessionTemplate(@Qualifier("ucenterSSF") SqlSessionFactory ucenterSqlSessionFactory){
        return new SqlSessionTemplate(ucenterSqlSessionFactory, ExecutorType.BATCH);
    }

    @Bean
    public MapperScannerConfigurer ucenterMapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionTemplateBeanName("ucenterSqlSessionTemplate");
        configurer.setBasePackage("com.weidai.dataMigration.dal.ucenter");
        return configurer;
    }

    @Bean
    @Qualifier("ucenterTM")
    public DataSourceTransactionManager ucenterTransactionManager(@Qualifier("ucenterDS") DataSource ucenterDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(ucenterDataSource);
        return transactionManager;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.ucore")
    @Qualifier("ucoreDS")
    public DataSource ucoreDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("ucoreSSF")
    public SqlSessionFactory ucoreSqlSessionFactory(@Qualifier("ucoreDS") DataSource ucoreDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:configuration.xml"));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/ucore/*.xml"));
        sqlSessionFactoryBean.setDataSource(ucoreDataSource);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    @Qualifier("ucoreSST")
    public SqlSessionTemplate ucoreSqlSessionTemplate(@Qualifier("ucoreSSF") SqlSessionFactory ucoreSqlSessionFactory){
        return new SqlSessionTemplate(ucoreSqlSessionFactory, ExecutorType.BATCH);
    }

    @Bean
    public MapperScannerConfigurer ucoreMapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionTemplateBeanName("ucoreSqlSessionTemplate");
        configurer.setBasePackage("com.weidai.dataMigration.dal.ucore");
        return configurer;
    }

    @Bean
    @Qualifier("ucoreTM")
    public DataSourceTransactionManager ucoreTransactionManager(@Qualifier("ucoreDS") DataSource ucoreDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(ucoreDataSource);
        return transactionManager;
    }
}
