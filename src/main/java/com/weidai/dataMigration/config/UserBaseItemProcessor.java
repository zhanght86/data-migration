/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuqi 2017/8/9 0009.
 */
public class UserBaseItemProcessor implements ItemProcessor<UserBaseDo, UserBaseDo> {
    
    public static AtomicInteger failedCount = new AtomicInteger(0);
    
    @Override
    public UserBaseDo process(UserBaseDo userBaseDo) throws Exception {
        if (StringUtils.hasText(userBaseDo.getMobile()) && isNotEmployee(userBaseDo)) {
            return userBaseDo;
        }
        failedCount.getAndIncrement();
        return null;
    }

    private boolean isNotEmployee(UserBaseDo userBaseDo) {
        return userBaseDo.getUserType() != null && UserTypeEnum.getEnumByCode(userBaseDo.getUserType()) != null;
    }
}
