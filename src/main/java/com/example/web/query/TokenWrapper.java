package com.example.web.query;

import javax.annotation.Resource;


public class TokenWrapper {
    private tokenBean value;
    private long expireTime;

    public tokenBean getValue() {
        return value;
    }

    public void setValue(tokenBean value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
