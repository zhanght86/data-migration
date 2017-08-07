package com.weidai.dataMigration.domain;

import java.util.Date;

public class UserBaseDo implements Comparable<UserBaseDo>{
    private Integer uid;

    private Date    createTime;

    private String  loginName;

    private String  userName;

    private String  password;

    private String  salt;

    private String  paypass;

    private Date    updateTime;

    private String  idNumber;

    private Integer realStatus;

    private Date    realStatusTime;

    private String  mobile;

    private Integer mobileStatus;

    private Integer marriage;

    private String  education;

    private String  email;

    private Integer emailStatus;

    private Integer sex;

    private Integer age;

    private String  headphoto;

    private String  birthday;

    private String  nation;

    private String  province;

    private String  city;

    private String  area;

    private Date    loginTime;

    private Integer loginNumber;

    private Integer ishave;

    private String  content;

    private String  addIp;

    private Integer status;

    private Integer way;

    private Integer uidSale;

    private String  emergencyName;

    private String  emergencyMobile;

    private String  occupation;

    private String  annualIncome;

    private Integer safePayPassword;

    /** 自然人id */
    private Integer gUid;

    /** 账户状态 0-非存管账户 1-存管账户*/
    private Integer depositoryStatus;

    /** 用户类型 1-投资人 2-借款人 3-中介 4-借+投 5-车商 6-房地产商 */
    private Integer userType;

    /**
     * 证件类型
     */
    private Integer idCardType;

    /**
     * 民事主体
     */
    private Integer civilSubjectType;

    /** 证件号 */
    public String   certNo;

    /** 昵称 */
    private String  nickName;

    /** 个人状态 */
    private Integer    isPersonal;

    private int rank;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPaypass() {
        return paypass;
    }

    public void setPaypass(String paypass) {
        this.paypass = paypass;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getRealStatus() {
        return realStatus;
    }

    public void setRealStatus(Integer realStatus) {
        this.realStatus = realStatus;
    }

    public Date getRealStatusTime() {
        return realStatusTime;
    }

    public void setRealStatusTime(Date realStatusTime) {
        this.realStatusTime = realStatusTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getMobileStatus() {
        return mobileStatus;
    }

    public void setMobileStatus(Integer mobileStatus) {
        this.mobileStatus = mobileStatus;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeadphoto() {
        return headphoto;
    }

    public void setHeadphoto(String headphoto) {
        this.headphoto = headphoto;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getLoginNumber() {
        return loginNumber;
    }

    public void setLoginNumber(Integer loginNumber) {
        this.loginNumber = loginNumber;
    }

    public Integer getIshave() {
        return ishave;
    }

    public void setIshave(Integer ishave) {
        this.ishave = ishave;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddIp() {
        return addIp;
    }

    public void setAddIp(String addIp) {
        this.addIp = addIp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public Integer getUidSale() {
        return uidSale;
    }

    public void setUidSale(Integer uidSale) {
        this.uidSale = uidSale;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyMobile() {
        return emergencyMobile;
    }

    public void setEmergencyMobile(String emergencyMobile) {
        this.emergencyMobile = emergencyMobile;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Integer getSafePayPassword() {
        return safePayPassword;
    }

    public void setSafePayPassword(Integer safePayPassword) {
        this.safePayPassword = safePayPassword;
    }

    public Integer getgUid() {
        return gUid;
    }

    public void setgUid(Integer gUid) {
        this.gUid = gUid;
    }

    public Integer getDepositoryStatus() {
        return depositoryStatus;
    }

    public void setDepositoryStatus(Integer depositoryStatus) {
        this.depositoryStatus = depositoryStatus;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public Integer getCivilSubjectType() {
        return civilSubjectType;
    }

    public void setCivilSubjectType(Integer civilSubjectType) {
        this.civilSubjectType = civilSubjectType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(Integer isPersonal) {
        this.isPersonal = isPersonal;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(UserBaseDo other) {
        if (other != null && rank < other.getRank()) {
            return -1;
        }
        if (other != null && rank > other.getRank()) {
            return 1;
        }
        return 0;
    }
}
