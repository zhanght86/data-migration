/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.service;

import com.weidai.dataMigration.config.FixedThreadPoolFactory;
import com.weidai.dataMigration.dal.ucenter.BorrowerDoMapper;
import com.weidai.dataMigration.dal.ucenter.TenderDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserBaseExtendDoMapper;
import com.weidai.dataMigration.dal.ucenter.UserRoleInfoDoMapper;
import com.weidai.dataMigration.dal.ucore.*;
import com.weidai.dataMigration.domain.*;
import com.weidai.dataMigration.util.UserMigrationHolder;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import com.weidai.ucore.facade.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.weidai.dataMigration.util.UserMigrationHolder.*;

/**
 * @author wuqi 2017/8/4 0004.
 */
@Service
public class UserMigrationService implements MigrationService<List<UserBaseDo>>, InitializingBean{

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
    
    public static ExecutorService executorService;

    @Override
    public void migrate(List<? extends List<UserBaseDo>> itemList, String type) {
        List<UserBaseDo> targetList = itemList.get(0);
        if (DEFAULT_TYPE.equals(type)) {
            if (!TAIL_ITEMS.isEmpty()) {
                targetList = mergeList(targetList);
            }
            if (!UserMigrationHolder.isLastPage()) {
                trimList(targetList);
            }
        }
        logger.info("migrate current batch data, size: {}", targetList.size());
        String[] markArr = { targetList.get(0).getMobile(), targetList.get(targetList.size() - 1).getMobile() };
        long preStart = System.currentTimeMillis();
        Map<String, UserInfoWrapper> userMap = new TreeMap<>();
        Set<Integer> uids = new HashSet<>(targetList.size());
        Set<Integer> borrowerIds = new HashSet<>();
        Set<Integer> tenderIds = new HashSet<>();
        for (UserBaseDo userBaseDo : targetList) {
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
                tenderIds.add(userBaseDo.getUid());
            }
        }
        if (userMap.isEmpty()) {
            return;
        }
        long start;
        if (!borrowerIds.isEmpty()) {
            start = System.currentTimeMillis();
            List<UserBaseExtendDo> userBaseExtendList = userBaseExtendDoMapper.selectUserBaseExtendIn(borrowerIds);
            logger.info("query u_base_extend costs: {}ms, borrowerUids size: {}", System.currentTimeMillis() - start, borrowerIds.size());

            start = System.currentTimeMillis();
            List<BorrowerDo> borrowerList = borrowerDoMapper.selectBorrowerIn(borrowerIds);
            logger.info("query u_borrower costs: {}ms, borrowerUids size: {}", System.currentTimeMillis() - start, borrowerIds.size());
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
            start = System.currentTimeMillis();
            List<TenderDo> tenderList = tenderDoMapper.selectTenderIn(tenderIds);
            logger.info("query u_tender costs: {}ms, tenderUids size: {}", System.currentTimeMillis() - start, tenderIds.size());
            if (tenderList != null && tenderList.size() > 0) {
                Map<Integer, TenderDo> tenderMap = transferTenderListToMap(tenderList);
                for (UserInfoWrapper wrapper : userMap.values()) {
                    if (wrapper.getTenderUid() != null && tenderMap.containsKey(wrapper.getTenderUid())) {
                        wrapper.setTenderDo(tenderMap.get(wrapper.getTenderUid()));
                    }
                }
            }
        }
        start = System.currentTimeMillis();
        List<RoleInfoDTO> roleInfoList = userRoleInfoDoMapper.selectRoleInfoIn(uids);
        logger.info("query u_role_info costs: {}ms, uid size: {}", System.currentTimeMillis() - start, uids.size());
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
        List<UserSubAccountDO> userSubAccountDOList = new ArrayList<>(targetList.size());
        List<LoginStatusDO> loginStatusDOList = new ArrayList<>(userMap.size());
        List<RegisterInfoDO> registerInfoDOList = new ArrayList<>(targetList.size());
        List<TenderInfoDO> tenderInfoDOList = new ArrayList<>();
        List<BorrowerInfoDO> borrowerInfoDOList = new ArrayList<>();
        for (UserInfoWrapper wrapper : userMap.values()) {
            wrapper.transferAllInfo();
            userDOList.add(wrapper.getUserDO());
            userExtendDOList.add(wrapper.getUserExtendDO());
            userSubAccountDOList.addAll(wrapper.getSubAccountList());
            loginStatusDOList.add(wrapper.getLoginStatus());
            registerInfoDOList.addAll(wrapper.getRegisterInfoList());
            if (wrapper.getTenderInfoDO() != null) {
                tenderInfoDOList.add(wrapper.getTenderInfoDO());
            }
            if (wrapper.getBorrowerInfoDO() != null) {
                borrowerInfoDOList.add(wrapper.getBorrowerInfoDO());
            }
        }
        logger.info("prepare data costs: {}ms", System.currentTimeMillis() - preStart);
        doMigrate(userDOList, userExtendDOList, userSubAccountDOList, loginStatusDOList, registerInfoDOList, tenderInfoDOList, borrowerInfoDOList, markArr, type);
    }

    private List<UserBaseDo> mergeList(List<UserBaseDo> list) {
        List<UserBaseDo> mergedList = new ArrayList<>(list.size());
        mergedList.addAll(TAIL_ITEMS);
        mergedList.addAll(list);
        TAIL_ITEMS.clear();
        return mergedList;
    }

    private void trimList(List<UserBaseDo> list) {
        UserBaseDo lastItem = list.remove(list.size() - 1);
        TAIL_ITEMS.add(lastItem);
        String cur = lastItem.getMobile();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!cur.equals(list.get(i).getMobile())) {
                break;
            }
            TAIL_ITEMS.add(list.remove(i));
        }
    }

    private void doMigrate(final List<UserDO> userDOList, final List<UserExtendDO> userExtendDOList, final List<UserSubAccountDO> userSubAccountDOList,
            final List<LoginStatusDO> loginStatusDOList, final List<RegisterInfoDO> registerInfoDOList, final List<TenderInfoDO> tenderInfoDOList,
            final List<BorrowerInfoDO> borrowerInfoDOList, final String[] markArr, String type) {
        if (!userDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, USER_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        userDOMapper.insertBatchWithId(userDOList);
                        logger.info("batch insert u_user costs: {}ms, size: {}", System.currentTimeMillis() - start, userDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}, start userId: {}, end userId: {}", USER_TYPE,
                                markArr[0], markArr[1], userDOList.get(0).getId(), userDOList.get(userDOList.size() - 1).getId());
                        logger.error(t.getMessage(), t);
                    }
                }
            });
        }
        if (!userExtendDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, USER_EXTEND_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        userExtendDOMapper.insertBatchWithUserId(userExtendDOList);
                        logger.info("batch insert u_user_extend costs: {}ms, size: {}", System.currentTimeMillis() - start, userExtendDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}, start userId: {}, end userId: {}",
                                USER_EXTEND_TYPE, markArr[0], markArr[1], userExtendDOList.get(0).getUserId(),
                                userExtendDOList.get(userExtendDOList.size() - 1).getUserId());
                        logger.error(t.getMessage(), t);
                    }
                }
            });
        }
        if (!userSubAccountDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, USER_SUB_ACCOUNT_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        userSubAccountDOMapper.insertBatchWithUid(userSubAccountDOList);
                        logger.info("batch insert u_sub_account costs: {}ms, size: {}", System.currentTimeMillis() - start, userSubAccountDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}, start userId: {}, end userId: {}",
                                USER_SUB_ACCOUNT_TYPE, markArr[0], markArr[1], userSubAccountDOList.get(0).getUserId(),
                                userSubAccountDOList.get(userSubAccountDOList.size() - 1).getUserId());
                        logger.error(t.getMessage(), t);
                    }
                }
            });
        }
        final CountDownLatch latch = new CountDownLatch(4);
        if (!loginStatusDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, LOGIN_STATUS_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        loginStatusDOMapper.insertBatch(loginStatusDOList);
                        logger.info("batch insert u_login_status costs: {}ms, size: {}", System.currentTimeMillis() - start, loginStatusDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}, start userId: {}, end userId: {}",
                                LOGIN_STATUS_TYPE, markArr[0], markArr[1], loginStatusDOList.get(0).getUserId(),
                                loginStatusDOList.get(loginStatusDOList.size() - 1).getUserId());
                        logger.error(t.getMessage(), t);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } else {
            latch.countDown();
        }
        if (!registerInfoDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, REGISTER_INFO_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        registerInfoDOMapper.insertBatch(registerInfoDOList);
                        logger.info("batch insert u_register_info costs: {}ms, size: {}", System.currentTimeMillis() - start, registerInfoDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}", REGISTER_INFO_TYPE, markArr[0], markArr[1]);
                        logger.error(t.getMessage(), t);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } else {
            latch.countDown();
        }
        if (!tenderInfoDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, TENDER_INFO_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        tenderInfoDOMapper.insertBatch(tenderInfoDOList);
                        logger.info("batch insert u_tender_info costs: {}ms, size: {}", System.currentTimeMillis() - start, tenderInfoDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}", TENDER_INFO_TYPE, markArr[0], markArr[1]);
                        logger.error(t.getMessage(), t);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } else {
            latch.countDown();
        }
        if (!borrowerInfoDOList.isEmpty() && (DEFAULT_TYPE.equals(type) || containsType(type, BORROWER_INFO_TYPE))) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        borrowerInfoDOMapper.insertBatch(borrowerInfoDOList);
                        logger.info("batch insert u_borrower_info costs: {}ms, size: {}", System.currentTimeMillis() - start, borrowerInfoDOList.size());
                    } catch (Throwable t) {
                        logger.error("execute current batch error, type: {}, start mobile: {}, end mobile: {}", BORROWER_INFO_TYPE, markArr[0], markArr[1]);
                        logger.error(t.getMessage(), t);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } else {
            latch.countDown();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("migration task was interrupted!", e);
        }
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

    private boolean containsType(String type, String val) {
        return Arrays.asList(type.split(",")).contains(val);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = FixedThreadPoolFactory.getInstance().getThreadPool(7, 15, "batch-insert-thread");
    }
}
