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

    public static final String DEFAULT_TYPE = "default";

    public static final String USER_TYPE = "user";

    public static final String USER_SUB_ACCOUNT_TYPE = "userSubAccount";

    public static final String USER_EXTEND_TYPE = "userExtend";

    public static final String TENDER_INFO_TYPE = "tenderInfo";

    public static final String BORROWER_INFO_TYPE = "borrowerInfo";

    public static final String REGISTER_INFO_TYPE = "registerInfo";

    public static final String LOGIN_STATUS_TYPE = "loginStatus";

    public static volatile Integer PAGE_SIZE;

    public static volatile Integer MAX_UID;

    public static volatile Integer TOTAL_PAGE = 0;

    public static volatile Integer CURRENT_PAGE = 0;

    public static final AtomicInteger INVALID_COUNT = new AtomicInteger(0);

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    private static final Map<String, Integer> CHANNEL_MAP = new HashMap<>();

    public static final List<UserBaseDo> TAIL_ITEMS = new ArrayList<>();

    public static int nextId() {
        return ID_GENERATOR.getAndIncrement();
    }

    public static void initId(int id){
        ID_GENERATOR.set(id);
    }

    public static void bindChannelMap(Map<String, Integer> targetMap) {
        CHANNEL_MAP.putAll(targetMap);
    }

    public static Integer getRegWay(String channelCode){
        return CHANNEL_MAP.get(channelCode);
    }

    public static boolean isLastPage() {
        return CURRENT_PAGE == TOTAL_PAGE - 1 || CURRENT_PAGE.intValue() == TOTAL_PAGE.intValue();
    }
}
