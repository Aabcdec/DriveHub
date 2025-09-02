package com.example.web.Servlet.Impl;

import com.alibaba.excel.EasyExcel;
import com.example.web.Bean.TClue;
import com.example.web.Listener.UploadDataListener;
import com.example.web.Mapper.TClueDao;
import com.example.web.Mapper.TCustomerDao;
import com.example.web.Servlet.TClueServlet;
import com.example.web.query.BaseQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Service
public class TClueServletImpl implements TClueServlet {
    @Resource
    private TClueDao tClueDao;
    @Resource
    private TCustomerDao tCustomerDao;
    @Override
    public TClue selectByPrimaryKey(Integer id) {
        return tClueDao.selectByPrimaryKey(id);
    }

    @Override
    public List<TClue> getClues(Integer pageNum, Integer pageSize) {
        // 计算偏移量，但不修改原始参数
        int offset = (pageNum - 1) * pageSize;
        return tClueDao.getClues(offset, pageSize, BaseQuery.builder().build());
    }
//    public List<TClue> getClues(Integer pageNum, Integer pageSize) {
//        //起始页等于页码减一*页面大小
//        pageNum=(pageNum-1)*pageSize;
//        return tClueDao.getClues(pageNum,pageSize, BaseQuery.builder().build());
//    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSelective(TClue tClue) {
        return tClueDao.insertSelective(tClue);
    }

    @Override
    public int updateByPrimaryKeySelective(TClue tClue) {
        return tClueDao.updateByPrimaryKeySelective(tClue);
    }

    @Override
    public List<TClue> selectByIdAndDateRange(TClue tClue) {

        return tClueDao.selectByIdAndDateRange(tClue);
    }

    @Override
    public void importExcel(InputStream inputStream,int create) {
        //链式编程，3个参数, 第一个参数是要读取的Excel文件，第二个参数是Excel模板类，第三个参数是文件读取的监听器
        EasyExcel.read(inputStream, TClue.class, new UploadDataListener(tClueDao,create))
                .sheet()
                .doRead();
    }

    @Override
    public int deteleByIdClue(Integer id) {

           //逻辑删除
            return tClueDao.updateDelete(id);

    }

    @Override
    public int updataHeader(Integer fid) {
        return tClueDao.updataHeader(fid);
    }

    @Override
    public List<TClue> overdueClueList(Integer ownerId) {
        return tClueDao.overdueClueList(ownerId);
    }
}
