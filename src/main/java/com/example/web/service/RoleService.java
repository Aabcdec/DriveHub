package com.example.web.service;

import com.example.web.Bean.TRole;
import com.example.web.Bean.TUser;
import com.example.web.Controller.TRoleController;

import java.util.List;

public interface RoleService {
    public TRole selectByPrimaryKey(Integer id);

    List<TUser> getRoleByUser(Integer roleId);

    int addRole(Integer id, List<Long> userIds);

    int addRoles(TRoleController.addRoleBean addRoleBean);

    List<TRole> getRoles();

    int updataRole(Integer id, TRoleController.addRoleBean addRoleBean);

    int deleteRole(Integer id);

    /** 兼容历史接口 GET /getroles */
    List<TRole> getRole();
}

