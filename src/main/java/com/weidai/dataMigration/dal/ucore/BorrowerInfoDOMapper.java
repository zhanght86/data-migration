/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucore;

import com.weidai.ucore.facade.domain.BorrowerInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuqi 2017/8/7 0007.
 */
public interface BorrowerInfoDOMapper {
    int insertBatch(@Param("borrowerInfoList") List<BorrowerInfoDO> borrowerInfoDOList);

    int deleteByUid(Integer uid);
}
