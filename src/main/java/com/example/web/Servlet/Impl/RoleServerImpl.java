package com.example.web.Servlet.Impl;

import com.example.web.Bean.TRole;
import com.example.web.Mapper.TRoleDao;
import com.example.web.Servlet.RoleServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServerImpl implements RoleServer {
    @Resource
    private TRoleDao tRoleDao;

    @Override
    public List<TRole> getRole() {
        return null;
    }
}
