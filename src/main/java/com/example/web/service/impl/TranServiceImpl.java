package com.example.web.service.impl;

import com.example.web.Bean.TTran;
import com.example.web.Mapper.TTranDao;
import com.example.web.service.CustomerService;
import com.example.web.service.TranService;
import com.example.web.query.StatisticsQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TranServiceImpl implements TranService {
    @Resource
    private TTranDao ttranDao;
    @Override
    public List<TTran> getTran(Integer pageNum, Integer pageSize) {
        pageNum=(pageNum-1)*pageSize;
        return ttranDao.getTran(pageNum,pageSize);
    }

    @Override
    public int updataTran(TTran tTran) {
        return ttranDao.updateByPrimaryKeySelective(tTran);
    }

    @Override
    public int addTran(TTran tTran) {
        return ttranDao.insertSelective(tTran);
    }

    @Override
    public StatisticsQuery getStatistics() {
        return ttranDao.getStatistics();
    }

    @Override
    public List<TTran> searchTran(TTran tTran) {
        System.out.println("servelet:"+tTran.toString());
        return ttranDao.searchTran(tTran);
    }


}
