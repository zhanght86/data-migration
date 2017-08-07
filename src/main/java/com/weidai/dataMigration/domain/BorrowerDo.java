package com.weidai.dataMigration.domain;

import java.util.Date;

public class BorrowerDo {
    private Integer id;

    private Integer uid;

    private Integer cid;

    private Integer uidAgency;

    private Integer isAllowBorrow;

    private Integer numAttestaiton;

    private Integer numPawn;

    private Integer bidMum;

    private Integer uidCus;

    private Boolean isCanTrust;

    private Integer wyrCid;

    private String purpose;

    private Integer borrowerType;

    private String borrowIntention;

    private String weixinAccount;

    private Date lastModfityTime;

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

    public Integer getUidAgency() {
        return uidAgency;
    }

    public void setUidAgency(Integer uidAgency) {
        this.uidAgency = uidAgency;
    }

    public Integer getIsAllowBorrow() {
        return isAllowBorrow;
    }

    public void setIsAllowBorrow(Integer isAllowBorrow) {
        this.isAllowBorrow = isAllowBorrow;
    }

    public Integer getNumAttestaiton() {
        return numAttestaiton;
    }

    public void setNumAttestaiton(Integer numAttestaiton) {
        this.numAttestaiton = numAttestaiton;
    }

    public Integer getNumPawn() {
        return numPawn;
    }

    public void setNumPawn(Integer numPawn) {
        this.numPawn = numPawn;
    }

    public Integer getBidMum() {
        return bidMum;
    }

    public void setBidMum(Integer bidMum) {
        this.bidMum = bidMum;
    }

    public Integer getUidCus() {
        return uidCus;
    }

    public void setUidCus(Integer uidCus) {
        this.uidCus = uidCus;
    }

    public Boolean getIsCanTrust() {
        return isCanTrust;
    }

    public void setIsCanTrust(Boolean isCanTrust) {
        this.isCanTrust = isCanTrust;
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

    public Integer getBorrowerType() {
        return borrowerType;
    }

    public void setBorrowerType(Integer borrowerType) {
        this.borrowerType = borrowerType;
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

    public Date getLastModfityTime() {
        return lastModfityTime;
    }

    public void setLastModfityTime(Date lastModfityTime) {
        this.lastModfityTime = lastModfityTime;
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