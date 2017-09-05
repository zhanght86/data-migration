/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucore;

import com.weidai.ucore.facade.domain.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuqi 2017/8/4 0004.
 */
public interface UserDOMapper {
    int insertBatchWithId(@Param("userList") List<UserDO> userList);

    Integer selectIdByMobile(String mobile);

    int deleteByPrimaryKey(Integer id);
}
