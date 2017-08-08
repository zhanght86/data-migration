/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuqi 2017/8/4 0004.
 */
public class UserMigrationHolder {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
    private static final Map<String, Integer> channelMap = new HashMap<>();

    public static int nextId() {
        return ID_GENERATOR.getAndIncrement();
    }

    public static void bindChannelMap(Map<String, Integer> targetMap) {
        channelMap.putAll(targetMap);
    }

    public static Integer getRegWay(String channelCode){
        return channelMap.get(channelCode);
    }
}
