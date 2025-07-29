package com.example.web.query;

public class marketQuery {
    private String  activeType;
    private int total;
    private int maxID;

    @Override
    public String toString() {
        return "marketQuery{" +
                "activeType='" + activeType + '\'' +
                ", total=" + total +
                ", maxID=" + maxID +
                '}';
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }

    public marketQuery() {
    }

    public marketQuery(String activeType, int total, int maxID) {
        this.activeType = activeType;
        this.total = total;
        this.maxID = maxID;
    }
}
