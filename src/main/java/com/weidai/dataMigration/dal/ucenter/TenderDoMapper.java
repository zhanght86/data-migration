/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.dal.ucenter;

import com.weidai.dataMigration.domain.TenderDo;

import java.util.List;
import java.util.Set;

/**
 * @author wuqi 2017/8/7 0007.
 */
public interface TenderDoMapper {
    List<TenderDo> selectTenderIn(Set<Integer> tenderIds);
}
