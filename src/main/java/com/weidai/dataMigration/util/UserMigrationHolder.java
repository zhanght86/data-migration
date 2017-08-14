/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.util;

import com.weidai.dataMigration.domain.UserBaseDo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuqi 2017/8/4 0004.
 */
public class UserMigrationHolder {

    public static final int PAGE_SIZE = 10_000;

    public static final AtomicInteger INVALID_COUNT = new AtomicInteger(0);

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    private static final Map<String, Integer> CHANNEL_MAP = new HashMap<>();

    public static final List<UserBaseDo> TAIL_ITEMS = new ArrayList<>();

    public static int nextId() {
        return ID_GENERATOR.getAndIncrement();
    }

    public static void bindChannelMap(Map<String, Integer> targetMap) {
        CHANNEL_MAP.putAll(targetMap);
    }

    public static Integer getRegWay(String channelCode){
        return CHANNEL_MAP.get(channelCode);
    }
}
