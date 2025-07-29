package com.example.web.Bean;

import java.io.Serializable;

/**
 * 角色权限关系表
 * t_role_permission
 */
public class TRolePermission implements Serializable {
    private Integer id;

    private Integer roleId;

    private Integer permissionId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}