package com.weidai.dataMigration.domain;

public class TenderDo {
    private Integer id;

    private Integer uid;

    private Integer uidCus;

    private Integer cid;

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