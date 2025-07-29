package com.example.web.Bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 * t_user
 */
@Data
public class TUser implements Serializable {
    /**
     * 主键，自动增长，用户ID
     */
    private Integer id;

    /**
     * 登录账号
     */
    private String loginAct;

    /**
     * 登录密码
     */
    private String loginPwd;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 账户是否没有过期，0已过期 1正常
     */
    private Integer accountNoExpired;

    /**
     * 密码是否没有过期，0已过期 1正常
     */
    private Integer credentialsNoExpired;

    /**
     * 账号是否没有锁定，0已锁定 1正常
     */
    private Integer accountNoLocked;

    /**
     * 账号是否启用，0禁用 1启用
     */
    private Integer accountEnabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 编辑人
     */
    private Integer editBy;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;
    private List<String> permissionList;
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "TUser{" +
                "id=" + id +
                ", loginAct='" + loginAct + '\'' +
                ", loginPwd='" + loginPwd + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", accountNoExpired=" + accountNoExpired +
                ", credentialsNoExpired=" + credentialsNoExpired +
                ", accountNoLocked=" + accountNoLocked +
                ", accountEnabled=" + accountEnabled +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                ", editTime=" + editTime +
                ", editBy=" + editBy +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAccountNoExpired() {
        return accountNoExpired;
    }

    public void setAccountNoExpired(Integer accountNoExpired) {
        this.accountNoExpired = accountNoExpired;
    }

    public Integer getCredentialsNoExpired() {
        return credentialsNoExpired;
    }

    public void setCredentialsNoExpired(Integer credentialsNoExpired) {
        this.credentialsNoExpired = credentialsNoExpired;
    }

    public Integer getAccountNoLocked() {
        return accountNoLocked;
    }

    public void setAccountNoLocked(Integer accountNoLocked) {
        this.accountNoLocked = accountNoLocked;
    }

    public Integer getAccountEnabled() {
        return accountEnabled;
    }

    public void setAccountEnabled(Integer accountEnabled) {
        this.accountEnabled = accountEnabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Integer getEditBy() {
        return editBy;
    }

    public void setEditBy(Integer editBy) {
        this.editBy = editBy;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public TUser(Integer id, String loginAct, String loginPwd, String name, String phone, String email, Integer accountNoExpired, Integer credentialsNoExpired, Integer accountNoLocked, Integer accountEnabled, Date createTime, Integer createBy, Date editTime, Integer editBy, Date lastLoginTime) {
        this.id = id;
        this.loginAct = loginAct;
        this.loginPwd = loginPwd;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.accountNoExpired = accountNoExpired;
        this.credentialsNoExpired = credentialsNoExpired;
        this.accountNoLocked = accountNoLocked;
        this.accountEnabled = accountEnabled;
        this.createTime = createTime;
        this.createBy = createBy;
        this.editTime = editTime;
        this.editBy = editBy;
        this.lastLoginTime = lastLoginTime;
    }

    public TUser() {
    }
}