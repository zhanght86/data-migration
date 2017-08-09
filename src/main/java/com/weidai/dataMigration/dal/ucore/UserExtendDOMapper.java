/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucore;

import com.weidai.ucore.facade.domain.UserExtendDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuqi 2017/8/4 0004.
 */
public interface UserExtendDOMapper {
    int insertBatchWithUserId(@Param("userExtendList") List<UserExtendDO> userExtendDOList);
}
