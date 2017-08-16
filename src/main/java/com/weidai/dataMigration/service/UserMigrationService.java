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
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

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
    
    @Autowired
    @Qualifier("ucoreSST")
    private SqlSessionTemplate sqlSessionTemplate;
    
    public static ExecutorService executorService;

    @Override
    public void migrate(List<? extends List<UserBaseDo>> itemList) {
        List<UserBaseDo> targetList = itemList.get(0);
        long preStart = System.currentTimeMillis();
        targetList = mergeList(targetList);
        if (!UserMigrationHolder.isLastPage()) {
            trimList(targetList);
        }
        logger.info("migrate current batch data, size: {}", targetList.size());
        Map<String, UserInfoWrapper> userMap = new HashMap<>(targetList.size());
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
                userMap.get(userBaseDo.getMobile()).setLoginName(userBaseDo.getLoginName());
                tenderIds.add(userBaseDo.getUid());
            }
        }
        if (userMap.isEmpty()) {
            return;
        }
        if (!borrowerIds.isEmpty()) {
            long ubStart = System.currentTimeMillis();
            List<UserBaseExtendDo> userBaseExtendList = userBaseExtendDoMapper.selectUserBaseExtendIn(borrowerIds);
            long ubEnd = System.currentTimeMillis();
            logger.info("query u_base_extend costs: {}ms, uid size: {}", ubEnd - ubStart, borrowerIds.size());

            long boStart = System.currentTimeMillis();
            List<BorrowerDo> borrowerList = borrowerDoMapper.selectBorrowerIn(borrowerIds);
            long boEnd = System.currentTimeMillis();
            logger.info("query u_borrower costs: {}ms, uid size: {}", boEnd - boStart, borrowerIds.size());
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
            long toStart = System.currentTimeMillis();
            List<TenderDo> tenderList = tenderDoMapper.selectTenderIn(tenderIds);
            long toEnd = System.currentTimeMillis();
            logger.info("query u_tender costs: {}ms, uid size: {}", toEnd - toStart, tenderIds.size());
            if (tenderList != null && tenderList.size() > 0) {
                Map<Integer, TenderDo> tenderMap = transferTenderListToMap(tenderList);
                for (UserInfoWrapper wrapper : userMap.values()) {
                    if (wrapper.getTenderUid() != null && tenderMap.containsKey(wrapper.getTenderUid())) {
                        wrapper.setTenderDo(tenderMap.get(wrapper.getTenderUid()));
                    }
                }
            }
        }
        long riStart = System.currentTimeMillis();
        List<RoleInfoDTO> roleInfoList = userRoleInfoDoMapper.selectRoleInfoIn(uids);
        long riEnd = System.currentTimeMillis();
        logger.info("query u_role_info costs: {}ms, uid size: {}", riEnd - riStart, uids.size());
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
        long preEnd = System.currentTimeMillis();
        logger.info("prepare data costs: {}ms", preEnd - preStart);
        doMigrate(userDOList, userExtendDOList, userSubAccountDOList, loginStatusDOList, registerInfoDOList, tenderInfoDOList, borrowerInfoDOList);
        long mEnd = System.currentTimeMillis();
        logger.info("batch insert data costs: {}ms", mEnd - preEnd);
    }

    private List<UserBaseDo> mergeList(List<UserBaseDo> list) {
        List<UserBaseDo> mergedList = new ArrayList<>(list.size());
        mergedList.addAll(UserMigrationHolder.TAIL_ITEMS);
        mergedList.addAll(list);
        UserMigrationHolder.TAIL_ITEMS.clear();
        return mergedList;
    }

    private void trimList(List<UserBaseDo> list) {
        UserBaseDo lastItem = list.remove(list.size() - 1);
        UserMigrationHolder.TAIL_ITEMS.add(lastItem);
        String cur = lastItem.getMobile();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!cur.equals(list.get(i).getMobile())) {
                break;
            }
            UserMigrationHolder.TAIL_ITEMS.add(list.remove(i));
        }
    }

    private void doMigrate(final List<UserDO> userDOList, final List<UserExtendDO> userExtendDOList, final List<UserSubAccountDO> userSubAccountDOList,
                           final List<LoginStatusDO> loginStatusDOList, final List<RegisterInfoDO> registerInfoDOList, final List<TenderInfoDO> tenderInfoDOList,
                           final List<BorrowerInfoDO> borrowerInfoDOList) {
        if (!userDOList.isEmpty()) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    long udStart = System.currentTimeMillis();
                    userDOMapper.insertBatchWithId(userDOList);
                    long udEnd = System.currentTimeMillis();
                    logger.info("batch insert u_user costs: {}ms, size: {}", udEnd - udStart, userDOList.size());
                }
            });
        }
        if (!userExtendDOList.isEmpty()) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    long uedStart = System.currentTimeMillis();
                    userExtendDOMapper.insertBatchWithUserId(userExtendDOList);
                    long uedEnd = System.currentTimeMillis();
                    logger.info("batch insert u_user_extend costs: {}ms, size: {}", uedEnd - uedStart, userExtendDOList.size());
                }
            });
        }
        if (!userSubAccountDOList.isEmpty()) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    long usdStart = System.currentTimeMillis();
                    userSubAccountDOMapper.insertBatchWithUid(userSubAccountDOList);
                    long usdEnd = System.currentTimeMillis();
                    logger.info("batch insert u_sub_account costs: {}ms, size: {}", usdEnd - usdStart, userSubAccountDOList.size());
                }
            });
        }
        final CountDownLatch latch = new CountDownLatch(4);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!loginStatusDOList.isEmpty()) {
                        long lsdStart = System.currentTimeMillis();
                        loginStatusDOMapper.insertBatch(loginStatusDOList);
                        long lsdEnd = System.currentTimeMillis();
                        logger.info("batch insert u_login_status costs: {}ms, size: {}", lsdEnd - lsdStart, loginStatusDOList.size());
                    }
                } finally {
                    latch.countDown();
                }
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!registerInfoDOList.isEmpty()) {
                        long ridStart = System.currentTimeMillis();
                        registerInfoDOMapper.insertBatch(registerInfoDOList);
                        long ridEnd = System.currentTimeMillis();
                        logger.info("batch insert u_register_info costs: {}ms, size: {}", ridEnd - ridStart, registerInfoDOList.size());
                    }
                } finally {
                    latch.countDown();
                }
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!tenderInfoDOList.isEmpty()) {
                        long tidStart = System.currentTimeMillis();
                        tenderInfoDOMapper.insertBatch(tenderInfoDOList);
                        long tidEnd = System.currentTimeMillis();
                        logger.info("batch insert u_tender_info costs: {}ms, size: {}", tidEnd - tidStart, tenderInfoDOList.size());
                    }
                } finally {
                    latch.countDown();
                }
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!borrowerInfoDOList.isEmpty()) {
                        long bidStart = System.currentTimeMillis();
                        borrowerInfoDOMapper.insertBatch(borrowerInfoDOList);
                        long bidEnd = System.currentTimeMillis();
                        logger.info("batch insert u_borrower_info costs: {}ms, size: {}", bidEnd - bidStart, borrowerInfoDOList.size());
                    }
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("migration thread was interrupted!", e);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = FixedThreadPoolFactory.getInstance().getThreadPool(7, 15, "batch-insert-thread");
    }
}
