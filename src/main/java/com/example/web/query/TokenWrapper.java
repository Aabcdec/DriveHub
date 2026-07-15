package com.example.web.query;

import javax.annotation.Resource;


public class TokenWrapper {
    private TokenBean value;
    private long expireTime;

    public TokenBean getValue() {
        return value;
    }

    public void setValue(TokenBean value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
