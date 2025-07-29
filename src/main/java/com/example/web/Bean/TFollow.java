package com.example.web.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TFollow {
    private int id;
    private String followType;
    private String followText;
    private int followState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private int createBy;
    private int fId;

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    @Override
    public String toString() {
        return "TFollow{" +
                "id=" + id +
                ", followType='" + followType + '\'' +
                ", followText='" + followText + '\'' +
                ", followState=" + followState +
                ", nextTime=" + nextTime +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                '}';
    }

    public TFollow(int id, String followType, String followText, int followState, Date nextTime, Date createTime, int createBy) {
        this.id = id;
        this.followType = followType;
        this.followText = followText;
        this.followState = followState;
        this.nextTime = nextTime;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    public TFollow() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }

    public String getFollowText() {
        return followText;
    }

    public void setFollowText(String followText) {
        this.followText = followText;
    }

    public int getFollowState() {
        return followState;
    }

    public void setFollowState(int followState) {
        this.followState = followState;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }
}
