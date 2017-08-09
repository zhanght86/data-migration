/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author wuqi 2017/8/9 0009.
 */
public class UserBaseItemProcessor implements ItemProcessor<UserBaseDo, UserBaseDo> {
    @Override
    public UserBaseDo process(UserBaseDo userBaseDo) throws Exception {
        if (userBaseDo.getMobile() != null && isNotEmployee(userBaseDo)) {
            return userBaseDo;
        }
        return null;
    }

    private boolean isNotEmployee(UserBaseDo userBaseDo) {
        return userBaseDo.getUserType() != null && UserTypeEnum.getEnumByCode(userBaseDo.getUserType()) != null;
    }
}
