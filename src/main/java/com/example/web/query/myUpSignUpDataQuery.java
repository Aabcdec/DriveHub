package com.example.web.query;

import java.util.List;

public class myUpSignUpDataQuery {
    private List<Long> ids;
    private String phone;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "myUpSignUpDataQuery{" +
                "ids=" + ids +
                ", phone='" + phone + '\'' +
                '}';
    }
}
