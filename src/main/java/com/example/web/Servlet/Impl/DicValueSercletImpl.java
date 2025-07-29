package com.example.web.Servlet.Impl;

import com.example.web.Bean.TDicValue;
import com.example.web.Mapper.TDicValueDao;
import com.example.web.Result.R;
import com.example.web.Servlet.DicValueServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DicValueSercletImpl implements DicValueServlet {
    @Resource
    private TDicValueDao tDicValueDao;
    @Override
    public R getValueByTyoeCode(Integer pageNum,Integer pageSize,String typeCode) {
      pageNum=(pageNum-1)*pageSize;
        return R.OK(tDicValueDao.selectByPrimaryKey(pageNum,pageSize,typeCode));
    }

    @Override
    public int addByTypeAddValue(TDicValue tDicValue) {
        return tDicValueDao.insert(tDicValue);
    }

    @Override
    public int deleteByIdValue(Integer id) {
        return tDicValueDao.deleteByPrimaryKey(id);
    }

    @Override
    public int PutByTypeAddValue(TDicValue tDicValue) {
        return tDicValueDao.updateByPrimaryKeySelective(tDicValue);
    }
}
