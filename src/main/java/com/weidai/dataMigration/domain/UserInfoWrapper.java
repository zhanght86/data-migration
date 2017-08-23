/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.domain;

import com.weidai.dataMigration.util.ThreeDESUtil;
import com.weidai.dataMigration.util.UserMigrationHolder;
import com.weidai.ucore.facade.constant.UserRegisterTypeEnum;
import com.weidai.ucore.facade.constant.UserTypeEnum;
import com.weidai.ucore.facade.domain.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/** 按照ucore结构梳理用户信息
 * @author wuqi 2017/8/7 0007.
 */
public class UserInfoWrapper {

    private List<UserBaseDo> userBaseList = new ArrayList<>();

    private UserBaseExtendDo userBaseExtendDo;

    private Integer borrowerUid;

    private UserBaseDo borrowerUserBaseDo;

    private BorrowerDo borrowerDo;

    private TenderDo tenderDo;

    private Integer tenderUid;

    private UserBaseDo tenderUserBaseDo;

    private String loginName;

    private Map<Integer, List<RoleInfoDTO>> roleInfoMap = new HashMap<>();

    private UserDO userDO;

    private UserExtendDO userExtendDO;

    private BorrowerInfoDO borrowerInfoDO;

    private TenderInfoDO tenderInfoDO;

    private LoginStatusDO loginStatus;

    private List<UserSubAccountDO> subAccountList;

    private List<RegisterInfoDO> registerInfoList = new ArrayList<>();

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
        // 转换投资人信息
        if (tenderDo != null) {
            transferTenderInfoDO();
        }
        // 转换借款人信息
        if (borrowerDo != null) {
            transferBorrowerInfoDO();
        }
        // 转换渠道信息
        transferRegisterInfoList();
    }

    /**
     * 转换渠道信息
     */
    private void transferRegisterInfoList() {
        for (UserBaseDo userBaseDo : userBaseList) {
            if (!roleInfoMap.isEmpty() && roleInfoMap.get(userBaseDo.getUid()) != null) {
                for (RoleInfoDTO roleInfoDTO : roleInfoMap.get(userBaseDo.getUid())) {
                    RegisterInfoDO registerInfoDO = new RegisterInfoDO();
                    BeanUtils.copyProperties(roleInfoDTO, registerInfoDO, "id");
                    registerInfoDO.setRegIp(userBaseDo.getAddIp());
                    registerInfoDO.setRegWay(getRegWay(registerInfoDO.getChannelCode()));
                    registerInfoDO.setFirstReg(0);
                    registerInfoList.add(registerInfoDO);
                }
            } else {
                RegisterInfoDO registerInfoDO = new RegisterInfoDO();
                registerInfoDO.setUid(userBaseDo.getUid());
                registerInfoDO.setUserRegisterType(UserRegisterTypeEnum.TENDER.getCode());
                registerInfoDO.setRegIp(userBaseDo.getAddIp());
                registerInfoDO.setChannelCode(userBaseDo.getContent());
                registerInfoDO.setRegWay(userBaseDo.getWay() == null ? getRegWay(userBaseDo.getContent()) : userBaseDo.getWay());
                registerInfoDO.setFirstReg(0);
                registerInfoDO.setRegTime(userBaseDo.getCreateTime());
                registerInfoList.add(registerInfoDO);
            }
        }
        Collections.sort(registerInfoList, new Comparator<RegisterInfoDO>() {
            @Override
            public int compare(RegisterInfoDO o1, RegisterInfoDO o2) {
                if (o1.getRegTime() == null && null == o2.getRegTime()) {
                    return 0;
                }
                if (o1.getRegTime() == null && o2.getRegTime() != null) {
                    return 1;
                }
                if (o1.getRegTime() != null && o2.getRegTime() == null) {
                    return -1;
                }
                if (o1.getRegTime().before(o2.getRegTime())) {
                    return -1;
                }
                if (o1.getRegTime().after(o2.getRegTime())) {
                    return 1;
                }
                return 0;
            }
        });
        registerInfoList.get(0).setFirstReg(1);
    }

    /**根据channelCode获取注册方式
     * @param channelCode
     * @return
     */
    private Integer getRegWay(String channelCode) {
        if (channelCode == null) {
            return null;
        }
        return UserMigrationHolder.getRegWay(channelCode);
    }

    /**
     * 转换借款人信息
     */
    private void transferBorrowerInfoDO() {
        borrowerInfoDO = new BorrowerInfoDO();
        BeanUtils.copyProperties(borrowerDo, borrowerInfoDO, "id", "borrowIntention");
        borrowerInfoDO.setWeChatAccount(borrowerDo.getWeixinAccount());
        borrowerInfoDO.setWeChatBindTime(borrowerDo.getBindWeixinTime());
        if (StringUtils.hasText(borrowerDo.getBorrowIntention())) {
            try {
                int borrowIntention = Integer.parseInt(borrowerDo.getBorrowIntention());
                if (borrowIntention <= 255 && borrowIntention >= 0) {
                    borrowerInfoDO.setBorrowIntention(borrowIntention);
                }
            } catch (Exception e) {}
        }
        if (userBaseExtendDo != null && userBaseExtendDo.getDrivingLicence() < 2) {
            borrowerInfoDO.setHasLicense(userBaseExtendDo.getDrivingLicence());
        }
        if (borrowerInfoDO.getHasLicense() == null) {
            borrowerInfoDO.setHasLicense(0);
        }
        borrowerInfoDO.setCreateTime(borrowerUserBaseDo.getCreateTime());
        borrowerInfoDO.setModifiedTime(borrowerUserBaseDo.getUpdateTime());
    }

    /**
     * 转换投资人信息
     */
    private void transferTenderInfoDO() {
        tenderInfoDO = new TenderInfoDO();
        BeanUtils.copyProperties(tenderDo, tenderInfoDO, "id");
        tenderInfoDO.setCreateTime(tenderUserBaseDo.getCreateTime());
        tenderInfoDO.setModifiedTime(tenderUserBaseDo.getUpdateTime());
    }

    /**转换用户扩展信息
     * @param primary
     */
    private void transferUserExtend(UserBaseDo primary) {
        userExtendDO = new UserExtendDO();
        userExtendDO.setUserId(userDO.getId());
        if (userBaseExtendDo != null) {
            BeanUtils.copyProperties(userBaseExtendDo, userExtendDO, "estateDesc", "carDesc", "workType", "compType", "localHousehold");
            userExtendDO.setCompName(userBaseExtendDo.getCompanyName());
            userExtendDO.setCreateTime(userBaseExtendDo.getCreatedTime());
            userExtendDO.setModifiedTime(userBaseExtendDo.getModifyTime());
            userExtendDO.setHouseInfo(userBaseExtendDo.getHouseEstate());
            if (StringUtils.hasText(userBaseExtendDo.getEstateDesc())) {
                try {
                    int estateDesc = Integer.parseInt(userBaseExtendDo.getEstateDesc());
                    if (estateDesc <= 255 && estateDesc >= 0) {
                        userExtendDO.setEstateDesc(estateDesc);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getCarDesc())) {
                try {
                    int carDesc = Integer.parseInt(userBaseExtendDo.getCarDesc());
                    if (carDesc <= 255 && carDesc >= 0) {
                        userExtendDO.setCarDesc(carDesc);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getWorkType())) {
                try {
                    int workType = Integer.parseInt(userBaseExtendDo.getWorkType());
                    if (workType <= 255 && workType >= 0) {
                        userExtendDO.setWorkType(workType);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getCompType())) {
                try {
                    int compType = Integer.parseInt(userBaseExtendDo.getCompType());
                    if (compType <= 255 && compType >= 0) {
                        userExtendDO.setCompType(compType);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getPayOffForm())) {
                try {
                    int payoffForm = Integer.parseInt(userBaseExtendDo.getPayOffForm());
                    if (payoffForm <= 255 && payoffForm >= 0) {
                        userExtendDO.setPayoffForm(payoffForm);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getJobTitle())) {
                try {
                    int jobLevel = Integer.parseInt(userBaseExtendDo.getJobTitle());
                    if (jobLevel <= 255 && jobLevel >= 0) {
                        userExtendDO.setJobLevel(jobLevel);
                    }
                } catch (Exception e) {
                }
            }
            if (StringUtils.hasText(userBaseExtendDo.getLocalHousehold())) {
                try {
                    int localHousehold = Integer.parseInt(userBaseExtendDo.getLocalHousehold());
                    if (localHousehold <= 255 && localHousehold >= 0) {
                        userExtendDO.setLocalHousehold(localHousehold);
                    }
                } catch (Exception e) {
                }
            }
        }
        userExtendDO.setEducation(primary.getEducation());
        if (primary.getMarriage() != null && primary.getMarriage() <= 255 && primary.getMarriage() >= 0) {
            userExtendDO.setMarriage(primary.getMarriage());
        }
        userExtendDO.setAssetSituation(primary.getIshave());
        userExtendDO.setEmergencyName(primary.getEmergencyName());
        userExtendDO.setEmergencyMobile(primary.getEmergencyMobile());
        userExtendDO.setOccupation(primary.getOccupation());
        userExtendDO.setAnnualIncome(primary.getAnnualIncome());
        if (userExtendDO.getCreateTime() == null) {
            userExtendDO.setCreateTime(primary.getCreateTime());
    }
        if (userExtendDO.getModifiedTime() == null) {
            userExtendDO.setModifiedTime(primary.getUpdateTime());
        }
        if (userExtendDO.getAdministrativeLevel() != null && userExtendDO.getAdministrativeLevel().length() > 2) {
            userExtendDO.setAdministrativeLevel(null);
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
        loginStatus.setCreateTime(puser.getCreateTime());
        loginStatus.setModifiedTime(puser.getUpdateTime());
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
            if (UserTypeEnum.U_TENDER.getCode().equals(userSubAccountDO.getUserType()) && tenderDo != null) {
                userSubAccountDO.setCid(tenderDo.getCid());
                userSubAccountDO.setCid(tenderDo.getUidCus());
            }
            if (UserTypeEnum.U_BORROWER.getCode().equals(userSubAccountDO.getUserType()) && borrowerDo != null) {
                userSubAccountDO.setCid(borrowerDo.getCid());
                userSubAccountDO.setUidCus(borrowerDo.getUidCus());
                userSubAccountDO.setWyrCid(borrowerDo.getWyrCid());
            }
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
        BeanUtils.copyProperties(primary, userDO, "age");
        if (primary.getAge() != null && primary.getAge() <= 255 && primary.getAge() >= 0) {
            userDO.setAge(primary.getAge());
        }
        userDO.setLoginName(loginName);
        if (userDO.getCertNo() == null && userDO.getIdNumber() != null) {
            userDO.setCertNo(ThreeDESUtil.encodeBASE64(userDO.getIdNumber()));
        }
        userDO.setHeadPhoto(primary.getHeadphoto());
        userDO.setLoginStatus(primary.getStatus());
        userDO.setGender(primary.getSex());
        userDO.setModifiedTime(primary.getUpdateTime());
        userDO.setRealStatus(primary.getRealStatus() == null ? 1 : primary.getRealStatus());
        userDO.setMobileStatus(primary.getMobileStatus() == null ? 1 : primary.getMobileStatus());
        userDO.setLoginStatus(primary.getStatus() == null ? 0 : primary.getStatus());
        if (userBaseExtendDo != null) {
            userDO.setVolk(userBaseExtendDo.getVolk());
        }
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
                rank += 2;
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

    public UserBaseDo getBorrowerUserBaseDo() {
        return borrowerUserBaseDo;
    }

    public void setBorrowerUserBaseDo(UserBaseDo borrowerUserBaseDo) {
        this.borrowerUserBaseDo = borrowerUserBaseDo;
    }

    public BorrowerDo getBorrowerDo() {
        return borrowerDo;
    }

    public void setBorrowerDo(BorrowerDo borrowerDo) {
        this.borrowerDo = borrowerDo;
    }

    public TenderDo getTenderDo() {
        return tenderDo;
    }

    public void setTenderDo(TenderDo tenderDo) {
        this.tenderDo = tenderDo;
    }

    public Integer getTenderUid() {
        return tenderUid;
    }

    public void setTenderUid(Integer tenderUid) {
        this.tenderUid = tenderUid;
    }

    public UserBaseDo getTenderUserBaseDo() {
        return tenderUserBaseDo;
    }

    public void setTenderUserBaseDo(UserBaseDo tenderUserBaseDo) {
        this.tenderUserBaseDo = tenderUserBaseDo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Map<Integer, List<RoleInfoDTO>> getRoleInfoMap() {
        return roleInfoMap;
    }

    public void setRoleInfoMap(Map<Integer, List<RoleInfoDTO>> roleInfoMap) {
        this.roleInfoMap = roleInfoMap;
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

    public BorrowerInfoDO getBorrowerInfoDO() {
        return borrowerInfoDO;
    }

    public void setBorrowerInfoDO(BorrowerInfoDO borrowerInfoDO) {
        this.borrowerInfoDO = borrowerInfoDO;
    }

    public TenderInfoDO getTenderInfoDO() {
        return tenderInfoDO;
    }

    public void setTenderInfoDO(TenderInfoDO tenderInfoDO) {
        this.tenderInfoDO = tenderInfoDO;
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
