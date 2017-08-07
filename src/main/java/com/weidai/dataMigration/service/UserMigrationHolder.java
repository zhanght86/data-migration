/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuqi 2017/8/4 0004.
 */
public class UserMigrationHolder {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    public static int nextId() {
        return ID_GENERATOR.getAndIncrement();
    }
}
