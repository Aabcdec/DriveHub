package com.example.web.Servlet.Impl;

import com.example.web.Bean.TRole;
import com.example.web.Bean.TUser;
import com.example.web.Controller.TRoleController;
import com.example.web.Mapper.TRoleDao;
import com.example.web.Servlet.TRoleServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TRoleServletImpl implements TRoleServlet {
    @Resource
    private TRoleDao tRoleDao;
    @Override
    public TRole selectByPrimaryKey(Integer id) {
        return tRoleDao.selectByPrimaryKey(id);
    }

    @Override
    public List<TUser> getRoleByUser(Integer roleId) {
        return tRoleDao.getRoleByUser(roleId);
    }

    @Override
    public int addRole(Integer id, List<Long> userIds) {
        return tRoleDao.addRole(id,userIds);
    }

    @Override
    public int addRoles(TRoleController.addRoleBean addRoleBean) {
        return tRoleDao.addRoles(addRoleBean);
    }

    @Override
    public List<TRole> getRoles() {
        return tRoleDao.getRoles();
    }

    @Override
    public int updataRole(Integer id, TRoleController.addRoleBean addRoleBean) {
        return tRoleDao.updateRole(id,addRoleBean);
    }

    @Override
    public int deleteRole(Integer id) {
        return tRoleDao.deleteByPrimaryKey(id);
    }
}
