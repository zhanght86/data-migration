/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuqi 2017/8/14 0014.
 */
public class DataMigrationItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationItemReader.class);

    private int pageSize = 10;

    private volatile int page = 0;

    private volatile boolean initialized = false;

    private final Object lock = new Object();

    private String queryId;

    private SqlSessionFactory sqlSessionFactory;

    private SqlSessionTemplate sqlSessionTemplate;

    private Map<String, Object> parameterValues;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public DataMigrationItemReader() {
        super.setName(ClassUtils.getShortName(DataMigrationItemReader.class));
    }

    @Override
    protected T doRead() throws Exception {
        synchronized (lock) {
            Map<String, Object> parameters = new HashMap<>();
            if (parameterValues != null) {
                parameters.putAll(parameterValues);
            }
            parameters.put("_page", page);
            parameters.put("_pagesize", pageSize);
            parameters.put("_skiprows", page * pageSize);
            long cur = System.currentTimeMillis();
            List<?> results = sqlSessionTemplate.selectList(queryId, parameters);
            logger.info("query No.{} page costs: {}ms", page + 1, System.currentTimeMillis() - cur);
            if (results != null && !results.isEmpty()) {
                page++;
                return (T) results;
            }
            return null;
        }
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.state(!initialized, "Cannot open an already opened ItemReader, call close first");
        initialized = true;
    }

    @Override
    protected void doClose() throws Exception {
        synchronized (lock) {
            initialized = false;
            page = 0;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(pageSize > 0, "pageSize must be greater than zero");
        Assert.notNull(sqlSessionFactory, "sqlSessionFactory mustn't be null");
        sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
        Assert.notNull(queryId, "queryId mustn't ne null");
    }
}
