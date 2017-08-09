package com.weidai.dataMigration.domain;

import java.util.Date;

public class BorrowerDo {
    private Integer id;

    private Integer uid;

    private Integer cid;

    private Integer uidCus;

    private Integer wyrCid;

    private String purpose;

    private String borrowIntention;

    private String weixinAccount;

    private Date bindWeixinTime;

    private String ciIdentificationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getUidCus() {
        return uidCus;
    }

    public void setUidCus(Integer uidCus) {
        this.uidCus = uidCus;
    }

    public Integer getWyrCid() {
        return wyrCid;
    }

    public void setWyrCid(Integer wyrCid) {
        this.wyrCid = wyrCid;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose == null ? null : purpose.trim();
    }

    public String getBorrowIntention() {
        return borrowIntention;
    }

    public void setBorrowIntention(String borrowIntention) {
        this.borrowIntention = borrowIntention == null ? null : borrowIntention.trim();
    }

    public String getWeixinAccount() {
        return weixinAccount;
    }

    public void setWeixinAccount(String weixinAccount) {
        this.weixinAccount = weixinAccount == null ? null : weixinAccount.trim();
    }

    public Date getBindWeixinTime() {
        return bindWeixinTime;
    }

    public void setBindWeixinTime(Date bindWeixinTime) {
        this.bindWeixinTime = bindWeixinTime;
    }

    public String getCiIdentificationId() {
        return ciIdentificationId;
    }

    public void setCiIdentificationId(String ciIdentificationId) {
        this.ciIdentificationId = ciIdentificationId == null ? null : ciIdentificationId.trim();
    }
}