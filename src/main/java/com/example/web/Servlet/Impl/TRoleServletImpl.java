package com.example.web.Servlet.Impl;

import com.example.web.Bean.TRole;
import com.example.web.Mapper.TRoleDao;
import com.example.web.Servlet.TRoleServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class TRoleServletImpl implements TRoleServlet {
    @Resource
    private TRoleDao tRoleDao;
    @Override
    public TRole selectByPrimaryKey(Integer id) {
        return tRoleDao.selectByPrimaryKey(id);
    }
}
