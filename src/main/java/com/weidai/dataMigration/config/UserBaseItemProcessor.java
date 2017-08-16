/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.config;

import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.util.UserMigrationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuqi 2017/8/9 0009.
 */
public class UserBaseItemProcessor implements ItemProcessor<List<UserBaseDo>, List<UserBaseDo>> {

    private static final Logger logger = LoggerFactory.getLogger(UserBaseItemProcessor.class);

    @Override
    public List<UserBaseDo> process(List<UserBaseDo> list) throws Exception {
        List<UserBaseDo> results = new ArrayList<>(list.size());
        long cur = System.currentTimeMillis();
        for (UserBaseDo userBaseDo : list) {
            if (StringUtils.hasText(userBaseDo.getMobile()) && isValid(userBaseDo) && isNotEmployee(userBaseDo)) {
                results.add(userBaseDo);
            } else {
                UserMigrationHolder.INVALID_COUNT.getAndIncrement();
            }
        }
        logger.info("UserBaseItemProcessor costs: {}ms", System.currentTimeMillis() - cur);
        return results.isEmpty() ? null : results;
    }

    private boolean isValid(UserBaseDo userBaseDo) {
        if (userBaseDo.getStatus() != null) {
            return userBaseDo.getStatus().equals(0) || userBaseDo.getStatus().equals(2);
        }
        return false;
    }

    private boolean isNotEmployee(UserBaseDo userBaseDo) {
        return userBaseDo.getUserType() != null && (userBaseDo.getUserType().equals(1) || userBaseDo.getUserType().equals(2)
                || userBaseDo.getUserType().equals(4) || userBaseDo.getUserType().equals(7) || userBaseDo.getUserType().equals(9));
    }
}
