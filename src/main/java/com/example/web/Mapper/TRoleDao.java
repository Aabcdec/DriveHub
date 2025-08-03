package com.example.web.Mapper;

import com.example.web.Bean.TRole;
import com.example.web.Bean.TUser;
import com.example.web.Controller.TRoleController;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TRoleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TRole record);

    int insertSelective(TRole record);

    TRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TRole record);

    int updateByPrimaryKey(TRole record);

    List<TUser> getRoleByUser(@Param("roleId") Integer roleId);

    int addRole(@Param("id") Integer id, @Param("userIds") List<Long> userIds);

    int addRoles(TRoleController.addRoleBean addRoleBean);

    List<TRole> getRoles();

    int updateRole(@Param("id") Integer id, @Param("addRoleBean") TRoleController.addRoleBean addRoleBean);

}