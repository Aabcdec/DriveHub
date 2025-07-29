package com.example.web.Servlet.Impl;

import com.example.web.Bean.TActivity;
import com.example.web.Mapper.TActivityDao;
import com.example.web.Servlet.ActivityServlet;
import com.example.web.query.BaseQuery;
import com.example.web.query.Images;
import com.example.web.query.myUpSignUpDataQuery;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Service
public class ActivityServletImpl implements ActivityServlet {
    @Resource
    private TActivityDao tActivityDao;

    @Override
    public List<TActivity> getActs(Integer pageNum, Integer pageSize) {
        System.out.println("测试缓存");
        System.out.println(pageNum+":"+pageSize);
        pageNum=(pageNum-1)*pageSize;
        System.out.println("pageNum"+pageNum);
        System.out.println("pageSize"+pageSize);
        return tActivityDao.getActs(pageNum,pageSize, BaseQuery.builder().build());
    }

    @Override
    public List<TActivity> getActAll() {
        return tActivityDao.getActAll();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateByPrimaryKeySelective(TActivity tActivity) {
        return tActivityDao.updateByPrimaryKeySelective(tActivity);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addTActivity(TActivity tActivity) {
        return tActivityDao.insertSelective(tActivity);
    }

    @Override
    public int deleteById(Integer id) {
        return tActivityDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<TActivity> queryByIdAndDate(Integer id, Date startTime, Date endTime) {
        // 1. 封装查询参数
        TActivity tActivity = new TActivity();
        tActivity.setId(id);
        tActivity.setStartTime(startTime);
        tActivity.setEndTime(endTime);

        // 2. 参数校验（确保ID非空）
        if (tActivity.getId() == null) {
            throw new RuntimeException("查询ID不能为空");
        }

        // 3. 调用Mapper查询
        return tActivityDao.selectByIdAndDateRange(tActivity);
    }

    @Override
    public List<TActivity> selectByIdsAct(List<Long> ids) {
        return tActivityDao.selectByIdsAct(ids);
    }

    @Override
    public TActivity selectByPrimaryKey(Integer id) {
        return tActivityDao.selectByPrimaryKey(id);
    }

    @Override
    public int increaseParticipants(Integer id) {
        return tActivityDao.increaseParticipants(id);
    }

    @Override
    public Integer updateParty(Integer id) {
        return tActivityDao.updateParty(id);
    }

    @Override
    public List<TActivity> selectByIdsSignUpData(myUpSignUpDataQuery myUpSignUpDataQuery) {
        return tActivityDao.selectByIdsSignUpData(myUpSignUpDataQuery);
    }


}
