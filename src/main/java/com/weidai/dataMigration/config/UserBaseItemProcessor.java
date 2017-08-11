/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.util.UserMigrationHolder;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

/**
 * @author wuqi 2017/8/9 0009.
 */
public class UserBaseItemProcessor implements ItemProcessor<UserBaseDo, UserBaseDo> {
    
    @Override
    public UserBaseDo process(UserBaseDo userBaseDo) throws Exception {
        if (StringUtils.hasText(userBaseDo.getMobile()) && isValid(userBaseDo) && isNotEmployee(userBaseDo)) {
            return userBaseDo;
        }
        UserMigrationHolder.INVALID_COUNT.getAndIncrement();
        return null;
    }

    private boolean isValid(UserBaseDo userBaseDo) {
        if (userBaseDo.getStatus() != null) {
            return userBaseDo.getStatus().equals(0) || userBaseDo.getStatus().equals(2);
        }
        return true;
    }

    private boolean isNotEmployee(UserBaseDo userBaseDo) {
        return userBaseDo.getUserType() != null && UserTypeEnum.getEnumByCode(userBaseDo.getUserType()) != null;
    }
}
