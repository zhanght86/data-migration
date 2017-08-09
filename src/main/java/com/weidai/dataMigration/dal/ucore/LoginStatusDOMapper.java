/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucore;

import com.weidai.ucore.facade.domain.LoginStatusDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuqi 2017/8/9 0009.
 */
public interface LoginStatusDOMapper {
    int insertBatch(@Param("loginStatusList") List<LoginStatusDO> loginStatusDOList);
}
