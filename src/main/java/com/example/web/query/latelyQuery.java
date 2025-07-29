package com.example.web.query;

import java.util.Date;

public class latelyQuery {
    private int id;
    private String actName;
    private String activeType;
    private Date startTime;
    private Date endTime;
    private int party;
    private String active;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "latelyQuery{" +
                "id=" + id +
                ", actName='" + actName + '\'' +
                ", activeType='" + activeType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", party=" + party +
                ", active='" + active + '\'' +
                '}';
    }

    public latelyQuery() {
    }

    public latelyQuery(int id, String actName, String activeType, Date startTime, Date endTime, int party, String active) {
        this.id = id;
        this.actName = actName;
        this.activeType = activeType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.party = party;
        this.active = active;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getParty() {
        return party;
    }

    public void setParty(int party) {
        this.party = party;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
