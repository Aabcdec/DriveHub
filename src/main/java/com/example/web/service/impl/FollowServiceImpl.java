package com.example.web.service.impl;

import com.example.web.Bean.TFollow;
import com.example.web.Mapper.TFollowDao;
import com.example.web.service.FollowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {


    @Resource
    private TFollowDao tFollowDao;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveFollow(TFollow tFollow) {
        return tFollowDao.saveFollow(tFollow);
    }

    @Override
    public List<TFollow> byIdFollow(Integer fId) {
        return tFollowDao.byIdFollow(fId);
    }

    @Override
    public int byDeleteIdFollow(Integer fId, Integer id) {
        return tFollowDao.byDeleteIdFollow(fId,id);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateFollow(TFollow tFollow) {
        return tFollowDao.tFollowDao(tFollow);
    }

    @Override
    public int deteleByIdFollow(Integer fId) {
        return tFollowDao.deteleByIdFollow(fId);
    }
}
