/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.domain;

import com.weidai.dataMigration.service.UserMigrationHolder;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import com.weidai.ucore.facade.domain.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** 按照ucore结构梳理用户信息
 * @author wuqi 2017/8/7 0007.
 */
public class UserInfoWrapper {

    private List<UserBaseDo> userBaseList = new ArrayList<>();

    private UserBaseExtendDo userBaseExtendDo;

    private Integer borrowerUid;

    private UserDO userDO;

    private UserExtendDO userExtendDO;

    private LoginStatusDO loginStatus;

    private List<UserSubAccountDO> subAccountList;

    private List<RegisterInfoDO> registerInfoList;

    public void transferAllInfo(){
        Assert.notEmpty(userBaseList, "userBaseList is empty.");
        // 转换主帐号
        UserBaseDo primary = choosePrimary();
        transferUserDO(primary);
        transferUserExtend(primary);
        // 转换子帐号
        transferSubAccountList();
        // 转换登录信息表
        UserBaseDo puser = choosePassword();
        transferLoginStatus(puser);
    }

    /**转换用户扩展信息
     * @param primary
     */
    private void transferUserExtend(UserBaseDo primary) {
        userExtendDO = new UserExtendDO();
        userExtendDO.setUserId(userDO.getId());
        if (userBaseExtendDo != null) {
            BeanUtils.copyProperties(userBaseExtendDo, userExtendDO);
            // todo
        }
    }

    /**
     * 转换登录信息
     * @param puser
     */
    private void transferLoginStatus(UserBaseDo puser) {
        loginStatus = new LoginStatusDO();
        loginStatus.setUserId(userDO.getId());
        loginStatus.setSalt(puser.getSalt());
        loginStatus.setPassword(puser.getPassword());
        loginStatus.setLoginNum(0);
        Date cur = new Date();
        loginStatus.setCreateTime(cur);
        loginStatus.setModifiedTime(cur);
    }

    /**
     * 转换子帐号信息
     * @return
     */
    private void transferSubAccountList() {
        subAccountList = new ArrayList<>(userBaseList.size());
        for (UserBaseDo userBaseDo : userBaseList) {
            UserSubAccountDO userSubAccountDO = new UserSubAccountDO();
            BeanUtils.copyProperties(userBaseDo, userSubAccountDO);
            userSubAccountDO.setGuid(userBaseDo.getgUid());
            userSubAccountDO.setDepositoryAcct(userBaseDo.getDepositoryStatus() == null ? 1 : userBaseDo.getDepositoryStatus());
            userSubAccountDO.setPersonal(userBaseDo.getIsPersonal() == null ? 0 : userBaseDo.getIsPersonal());
            userSubAccountDO.setPayPassword(userBaseDo.getPaypass());
            userSubAccountDO.setModifiedTime(userBaseDo.getUpdateTime());
            userSubAccountDO.setUserId(userDO.getId());
            subAccountList.add(userSubAccountDO);
        }
    }

    /**
     * 转换主帐号信息
     * @param primary
     * @return
     */
    private void transferUserDO(UserBaseDo primary) {
        userDO = new UserDO();
        BeanUtils.copyProperties(primary, userDO);
        userDO.setHeadPhoto(primary.getHeadphoto());
        userDO.setLoginStatus(primary.getStatus());
        userDO.setGender(primary.getSex());
        if (userBaseExtendDo != null) {
            userDO.setVolk(userBaseExtendDo.getVolk());
        }
        Date cur = new Date();
        userDO.setCreateTime(cur);
        userDO.setModifiedTime(cur);
        userDO.setId(UserMigrationHolder.nextId());
    }

    private UserBaseDo choosePassword() {
        if (userBaseList.size() == 1) {
            return userBaseList.get(0);
        }
        for (UserBaseDo userBaseDo : userBaseList) {
            int rank;
            if (UserTypeEnum.U_TENDER.getCode().equals(userBaseDo.getUserType())) {
                rank = 4;
            } else if (UserTypeEnum.U_BORROWER.getCode().equals(userBaseDo.getUserType())) {
                rank = 2;
            } else {
                rank = 1;
            }
            userBaseDo.setRank(rank);
        }
        Collections.sort(userBaseList);
        return userBaseList.get(userBaseList.size() - 1);
    }

    private UserBaseDo choosePrimary() {
        if (userBaseList.size() == 1) {
            return userBaseList.get(0);
        }
        for (UserBaseDo userBaseDo : userBaseList) {
            int rank = 0;
            if (userBaseDo.getRealStatus() != null && userBaseDo.getRealStatus().equals(0)) {
                rank++;
            }
            if (UserTypeEnum.U_BORROWER.getCode().equals(userBaseDo.getUserType())) {
                rank++;
            }
            userBaseDo.setRank(rank);
        }
        Collections.sort(userBaseList);
        return userBaseList.get(userBaseList.size() - 1);
    }

    public List<UserBaseDo> getUserBaseList() {
        return userBaseList;
    }

    public void setUserBaseList(List<UserBaseDo> userBaseList) {
        this.userBaseList = userBaseList;
    }

    public UserBaseExtendDo getUserBaseExtendDo() {
        return userBaseExtendDo;
    }

    public void setUserBaseExtendDo(UserBaseExtendDo userBaseExtendDo) {
        this.userBaseExtendDo = userBaseExtendDo;
    }

    public Integer getBorrowerUid() {
        return borrowerUid;
    }

    public void setBorrowerUid(Integer borrowerUid) {
        this.borrowerUid = borrowerUid;
    }

    public UserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(UserDO userDO) {
        this.userDO = userDO;
    }

    public UserExtendDO getUserExtendDO() {
        return userExtendDO;
    }

    public void setUserExtendDO(UserExtendDO userExtendDO) {
        this.userExtendDO = userExtendDO;
    }

    public LoginStatusDO getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatusDO loginStatus) {
        this.loginStatus = loginStatus;
    }

    public List<UserSubAccountDO> getSubAccountList() {
        return subAccountList;
    }

    public void setSubAccountList(List<UserSubAccountDO> subAccountList) {
        this.subAccountList = subAccountList;
    }

    public List<RegisterInfoDO> getRegisterInfoList() {
        return registerInfoList;
    }

    public void setRegisterInfoList(List<RegisterInfoDO> registerInfoList) {
        this.registerInfoList = registerInfoList;
    }
}
