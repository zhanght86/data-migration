/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.service;

import com.weidai.dataMigration.dal.ucenter.BorrowerDoMapper;
import com.weidai.dataMigration.dal.ucenter.TenderDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserBaseExtendDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserRoleInfoDoMapper;
import com.weidai.dataMigration.dal.ucore.*;
import com.weidai.dataMigration.domain.*;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import com.weidai.ucore.facade.domain.*;
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
    private LoginStatusDOMapper loginStatusDOMapper;

    @Autowired
    private UserRoleInfoDoMapper userRoleInfoDoMapper;

    @Autowired
    private RegisterInfoDOMapper registerInfoDOMapper;

    @Autowired
    private BorrowerDoMapper borrowerDoMapper;

    @Autowired
    private TenderDoMapper tenderDoMapper;

    @Autowired
    private TenderInfoDOMapper tenderInfoDOMapper;

    @Autowired
    private BorrowerInfoDOMapper borrowerInfoDOMapper;

    @Override
    public void migrate(List<? extends UserBaseDo> itemList) {
        Map<String, UserInfoWrapper> userMap = new HashMap<>(itemList.size());
        Set<Integer> uids = new HashSet<>(itemList.size());
        Set<Integer> borrowerIds = new HashSet<>();
        Set<Integer> tenderIds = new HashSet<>();
        for (UserBaseDo userBaseDo : itemList) {
            if (!userMap.containsKey(userBaseDo.getMobile())) {
                userMap.put(userBaseDo.getMobile(), new UserInfoWrapper());
            }
            userMap.get(userBaseDo.getMobile()).getUserBaseList().add(userBaseDo);
            uids.add(userBaseDo.getUid());
            if (UserTypeEnum.U_BORROWER.getCode().equals(userBaseDo.getUserType())) {
                userMap.get(userBaseDo.getMobile()).setBorrowerUid(userBaseDo.getUid());
                userMap.get(userBaseDo.getMobile()).setBorrowerUserBaseDo(userBaseDo);
                borrowerIds.add(userBaseDo.getUid());
            } else if (UserTypeEnum.U_TENDER.getCode().equals(userBaseDo.getUserType())) {
                userMap.get(userBaseDo.getMobile()).setTenderUid(userBaseDo.getUid());
                userMap.get(userBaseDo.getMobile()).setTenderUserBaseDo(userBaseDo);
                userMap.get(userBaseDo.getMobile()).setLoginName(userBaseDo.getLoginName());
                tenderIds.add(userBaseDo.getUid());
            }
        }
        if (userMap.isEmpty()) {
            return;
        }
        if (!borrowerIds.isEmpty()) {
            List<UserBaseExtendDo> userBaseExtendList = userBaseExtendDoMapper.selectUserBaseExtendIn(borrowerIds);
            List<BorrowerDo> borrowerList = borrowerDoMapper.selectBorrowerIn(borrowerIds);
            if ((userBaseExtendList != null && userBaseExtendList.size() > 0) || (borrowerList != null && borrowerList.size() > 0)) {
                Map<Integer, UserBaseExtendDo> userBaseExtendMap = transferUserBaseExtendListToMap(userBaseExtendList);
                Map<Integer, BorrowerDo> borrowerMap = transferBorrowerListToMap(borrowerList);
                for (UserInfoWrapper wrapper : userMap.values()) {
                    if (wrapper.getBorrowerUid() != null) {
                        if (!userBaseExtendMap.isEmpty() && userBaseExtendMap.containsKey(wrapper.getBorrowerUid())) {
                            wrapper.setUserBaseExtendDo(userBaseExtendMap.get(wrapper.getBorrowerUid()));
                        }
                        if (!borrowerMap.isEmpty() && borrowerMap.containsKey(wrapper.getBorrowerUid())) {
                            wrapper.setBorrowerDo(borrowerMap.get(wrapper.getBorrowerUid()));
                        }
                    }
                }
            }
        }
        if (!tenderIds.isEmpty()) {
            List<TenderDo> tenderList = tenderDoMapper.selectTenderIn(tenderIds);
            if (tenderList != null && tenderList.size() > 0) {
                Map<Integer, TenderDo> tenderMap = transferTenderListToMap(tenderList);
                for (UserInfoWrapper wrapper : userMap.values()) {
                    if (wrapper.getTenderUid() != null && tenderMap.containsKey(wrapper.getTenderUid())) {
                        wrapper.setTenderDo(tenderMap.get(wrapper.getTenderUid()));
                    }
                }
            }
        }
        List<RoleInfoDTO> roleInfoList = userRoleInfoDoMapper.selectRoleInfoIn(uids);
        if (roleInfoList != null && roleInfoList.size() > 0) {
            Map<Integer, List<RoleInfoDTO>> roleInfoMap = transferRoleInfoListToMap(roleInfoList);
            for (UserInfoWrapper wrapper : userMap.values()) {
                for (UserBaseDo userBaseDo : wrapper.getUserBaseList()) {
                    if (roleInfoMap.containsKey(userBaseDo.getUid())) {
                        wrapper.getRoleInfoMap().put(userBaseDo.getUid(), roleInfoMap.get(userBaseDo.getUid()));
                    }
                }
            }
        }
        List<UserDO> userDOList = new ArrayList<>(userMap.size());
        List<UserExtendDO> userExtendDOList = new ArrayList<>(userMap.size());
        List<UserSubAccountDO> userSubAccountDOList = new ArrayList<>(itemList.size());
        List<LoginStatusDO> loginStatusDOList = new ArrayList<>(userMap.size());
        List<RegisterInfoDO> registerInfoDOList = new ArrayList<>(itemList.size());
        List<TenderInfoDO> tenderInfoDOList = new ArrayList<>();
        List<BorrowerInfoDO> borrowerInfoDOList = new ArrayList<>();
        for (UserInfoWrapper wrapper : userMap.values()) {
            wrapper.transferAllInfo();
            userDOList.add(wrapper.getUserDO());
            userExtendDOList.add(wrapper.getUserExtendDO());
            userSubAccountDOList.addAll(wrapper.getSubAccountList());
            loginStatusDOList.add(wrapper.getLoginStatus());
            registerInfoDOList.addAll(wrapper.getRegisterInfoList());
            tenderInfoDOList.add(wrapper.getTenderInfoDO());
            borrowerInfoDOList.add(wrapper.getBorrowerInfoDO());
        }
        doMigrate(userDOList, userExtendDOList, userSubAccountDOList, loginStatusDOList, registerInfoDOList, tenderInfoDOList, borrowerInfoDOList);
    }

    @Transactional(value = "ucoreTM")
    private void doMigrate(List<UserDO> userDOList, List<UserExtendDO> userExtendDOList, List<UserSubAccountDO> userSubAccountDOList,
            List<LoginStatusDO> loginStatusDOList, List<RegisterInfoDO> registerInfoDOList, List<TenderInfoDO> tenderInfoDOList,
            List<BorrowerInfoDO> borrowerInfoDOList) {
        userDOMapper.insertBatchWithId(userDOList);
        userExtendDOMapper.insertBatchWithUserId(userExtendDOList);
        userSubAccountDOMapper.insertBatchWithUid(userSubAccountDOList);
        loginStatusDOMapper.insertBatch(loginStatusDOList);
        registerInfoDOMapper.insertBatch(registerInfoDOList);
        tenderInfoDOMapper.insertBatch(tenderInfoDOList);
        borrowerInfoDOMapper.insertBatch(borrowerInfoDOList);
    }

    private Map<Integer, List<RoleInfoDTO>> transferRoleInfoListToMap(List<RoleInfoDTO> roleInfoList) {
        Map<Integer, List<RoleInfoDTO>> map = new HashMap<>(roleInfoList.size());
        for (RoleInfoDTO roleInfoDTO : roleInfoList) {
            if (!map.containsKey(roleInfoDTO.getUid())) {
                map.put(roleInfoDTO.getUid(), new ArrayList<RoleInfoDTO>());
            }
            map.get(roleInfoDTO.getUid()).add(roleInfoDTO);
        }
        return map;
    }

    private Map<Integer, TenderDo> transferTenderListToMap(List<TenderDo> tenderList) {
        Map<Integer, TenderDo> map = new HashMap<>(tenderList.size());
        for (TenderDo tenderDo : tenderList) {
            map.put(tenderDo.getUid(), tenderDo);
        }
        return map;
    }

    private Map<Integer, BorrowerDo> transferBorrowerListToMap(List<BorrowerDo> borrowerList) {
        Map<Integer, BorrowerDo> map = new HashMap<>(borrowerList.size());
        for (BorrowerDo borrowerDo : borrowerList) {
            map.put(borrowerDo.getUid(), borrowerDo);
        }
        return map;
    }

    private Map<Integer, UserBaseExtendDo> transferUserBaseExtendListToMap(List<UserBaseExtendDo> list) {
        Map<Integer, UserBaseExtendDo> map = new HashMap<>(list.size());
        for (UserBaseExtendDo userBaseExtendDo : list) {
            map.put(userBaseExtendDo.getUid(), userBaseExtendDo);
        }
        return map;
    }
}
