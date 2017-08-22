/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucenter;

import com.weidai.dataMigration.domain.UserBaseDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuqi 2017/8/4 0004.
 */
public interface UserBaseDoMapper {
    int count(@Param("maxUid") Integer maxUid);

    List<UserBaseDo> selectBetween(@Param("startMobile") String startMobile, @Param("endMobile") String endMobile, @Param("maxUid") Integer maxUid);
}
