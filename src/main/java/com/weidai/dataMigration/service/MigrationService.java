/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.service;

import java.util.List;

/**
 * @author wuqi 2017/8/4 0004.
 */
public interface MigrationService<T> {
    /**
     * 迁移数据
     */
    void migrate(List<? extends T> itemList, String type);
}
