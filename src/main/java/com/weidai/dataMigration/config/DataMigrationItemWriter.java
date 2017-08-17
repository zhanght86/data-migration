/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.service.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 自定义的ItemWriter，取代MybatisBatchItemWriter，
 * 使用MySql语法特性插入：insert into table values(), values()
 * @author wuqi 2017/8/4 0004.
 */
public class DataMigrationItemWriter<T> implements ItemWriter<T>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationItemWriter.class);

    private MigrationService<T> migrationService;

    public void setMigrationService(MigrationService<T> migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        if (!items.isEmpty()) {
            migrationService.migrate(items, "default");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(migrationService, "A MigrationService is required.");
    }
}
