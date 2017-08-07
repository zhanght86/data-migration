package com.weidai.dataMigration.domain;

import java.util.Date;

public class TenderDo {
    private Integer id;

    private Integer uid;

    private Integer uidCus;

    private Integer cid;

    private Integer autoBidNum;

    private Integer setBidAutoNum;

    private Integer cidBelong;

    private Date lastModfityTime;

    private Integer riskEvaluationScore;

    private Integer riskEvaluationType;

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

    public Integer getUidCus() {
        return uidCus;
    }

    public void setUidCus(Integer uidCus) {
        this.uidCus = uidCus;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getAutoBidNum() {
        return autoBidNum;
    }

    public void setAutoBidNum(Integer autoBidNum) {
        this.autoBidNum = autoBidNum;
    }

    public Integer getSetBidAutoNum() {
        return setBidAutoNum;
    }

    public void setSetBidAutoNum(Integer setBidAutoNum) {
        this.setBidAutoNum = setBidAutoNum;
    }

    public Integer getCidBelong() {
        return cidBelong;
    }

    public void setCidBelong(Integer cidBelong) {
        this.cidBelong = cidBelong;
    }

    public Date getLastModfityTime() {
        return lastModfityTime;
    }

    public void setLastModfityTime(Date lastModfityTime) {
        this.lastModfityTime = lastModfityTime;
    }

    public Integer getRiskEvaluationScore() {
        return riskEvaluationScore;
    }

    public void setRiskEvaluationScore(Integer riskEvaluationScore) {
        this.riskEvaluationScore = riskEvaluationScore;
    }

    public Integer getRiskEvaluationType() {
        return riskEvaluationType;
    }

    public void setRiskEvaluationType(Integer riskEvaluationType) {
        this.riskEvaluationType = riskEvaluationType;
    }
}