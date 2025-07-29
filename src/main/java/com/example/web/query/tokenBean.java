package com.example.web.query;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;


public class tokenBean {
    private Integer id;
    private String token;
    private String name;

    @Override
    public String toString() {
        return "tokenBean{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public tokenBean(Integer id, String token, String name) {
        this.id = id;
        this.token = token;
        this.name = name;
    }

    public tokenBean() {
    }
}
