/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.service;

import com.weidai.dataMigration.dal.ucenter.UserBaseExtendDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserChannelInfoDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserRoleInfoDoMapper;
import com.weidai.dataMigration.dal.ucore.UserDOMapper;
import com.weidai.dataMigration.dal.ucore.UserExtendDOMapper;
import com.weidai.dataMigration.dal.ucore.UserSubAccountDOMapper;
import com.weidai.dataMigration.domain.UserBaseDo;
import com.weidai.dataMigration.domain.UserBaseExtendDo;
import com.weidai.dataMigration.domain.UserInfoWrapper;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author wuqi 2017/8/4 0004.
 */
@Service
public class UserMigrationService implements MigrationService<UserBaseDo> {

    private static final Logger logger = LoggerFactory.getLogger(UserMigrationService.class);

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserSubAccountDOMapper userSubAccountDOMapper;

    @Autowired
    private UserExtendDOMapper userExtendDOMapper;

    @Autowired
    private UserBaseExtendDoMapper userBaseExtendDoMapper;

    @Autowired
    private UserRoleInfoDoMapper userRoleInfoDoMapper;

    @Autowired
    private UserChannelInfoDoMapper userChannelInfoDoMapper;

    @Override
    @Transactional(value = "ucoreTM")
    public void migrate(List<? extends UserBaseDo> itemList) {
        Map<String, UserInfoWrapper> userMap = new HashMap<>(itemList.size());
        Set<Integer> borrowerIds = new HashSet<>();
        for (UserBaseDo userBaseDo : itemList) {
            if (userBaseDo.getMobile() != null && isNotEmployee(userBaseDo)) {
                if (!userMap.containsKey(userBaseDo.getMobile())) {
                    userMap.put(userBaseDo.getMobile(), new UserInfoWrapper());
                }
                userMap.get(userBaseDo.getMobile()).getUserBaseList().add(userBaseDo);
                if (UserTypeEnum.U_BORROWER.getCode().equals(userBaseDo.getUserType())) {
                    userMap.get(userBaseDo.getMobile()).setBorrowerUid(userBaseDo.getUid());
                    borrowerIds.add(userBaseDo.getUid());
                }
            }
        }
        if (userMap.isEmpty()) {
            return;
        }
        if (!borrowerIds.isEmpty()) {
            List<UserBaseExtendDo> list = userBaseExtendDoMapper.selectUserBaseExtendIn(borrowerIds);
            if (list != null && list.size() > 0) {
                Map<Integer, UserBaseExtendDo> userBaseExtendMap = transferListToMap(list);
                for (UserInfoWrapper wrapper : userMap.values()) {
                    if (wrapper.getBorrowerUid() != null && userBaseExtendMap.containsKey(wrapper.getBorrowerUid())) {
                        wrapper.setUserBaseExtendDo(userBaseExtendMap.get(wrapper.getBorrowerUid()));
                    }
                }
            }
        }
        for (UserInfoWrapper wrapper : userMap.values()) {
            wrapper.transferAllInfo();
        }
    }

    private Map<Integer, UserBaseExtendDo> transferListToMap(List<UserBaseExtendDo> list) {
        Map<Integer, UserBaseExtendDo> map = new HashMap<>(list.size());
        for (UserBaseExtendDo userBaseExtendDo : list) {
            map.put(userBaseExtendDo.getUid(), userBaseExtendDo);
        }
        return map;
    }

    private boolean isNotEmployee(UserBaseDo userBaseDo) {
        return userBaseDo.getUserType() != null && UserTypeEnum.getEnumByCode(userBaseDo.getUserType()) != null;
    }
}
