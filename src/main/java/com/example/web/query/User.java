package com.example.web.query;

public class User {
    private String loginAct;
    public String loginPwd;

    @Override
    public String toString() {
        return "User{" +
                "loginAct='" + loginAct + '\'' +
                ", loginPwd='" + loginPwd + '\'' +
                '}';
    }

    public User(String loginAct, String loginPwd) {
        this.loginAct = loginAct;
        this.loginPwd = loginPwd;
    }

    public User() {
    }

    public String getLoginAct() {
        return loginAct;
    }

    public void setLoginAct(String loginAct) {
        this.loginAct = loginAct;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }
}
