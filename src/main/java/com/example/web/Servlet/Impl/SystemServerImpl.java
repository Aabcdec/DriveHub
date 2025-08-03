package com.example.web.Servlet.Impl;

import com.example.web.Bean.TSystemInfo;
import com.example.web.Mapper.TSystemInfoDao;
import com.example.web.Servlet.SystemServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SystemServerImpl implements SystemServer {
    @Resource
    private TSystemInfoDao tSystemInfoDao;
    @Override
    public List<TSystemInfo> getSystemInfos() {
        return tSystemInfoDao.getSystemInfos();
    }

    @Override
    public int updateSystemInfo(TSystemInfo tSystemInfo) {
        return tSystemInfoDao.updateByPrimaryKeySelective(tSystemInfo);
    }
}
