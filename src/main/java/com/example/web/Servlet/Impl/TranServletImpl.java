package com.example.web.Servlet.Impl;

import com.example.web.Bean.TTran;
import com.example.web.Mapper.TTranDao;
import com.example.web.Servlet.TCudtomerServlet;
import com.example.web.Servlet.TranServlet;
import com.example.web.query.StatisticsQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TranServletImpl implements TranServlet {
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
