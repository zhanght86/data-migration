/**
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.domain;

import java.util.Date;

/**
 * @author wuqi 2017/8/7 0007.
 */
public class RoleInfoDTO {

    private Integer uid;

    private Integer userRegisterType;

    private String channelCode;

    private String keyword;

    private Date regTime;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getUserRegisterType() {
        return userRegisterType;
    }

    public void setUserRegisterType(Integer userRegisterType) {
        this.userRegisterType = userRegisterType;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }
}
