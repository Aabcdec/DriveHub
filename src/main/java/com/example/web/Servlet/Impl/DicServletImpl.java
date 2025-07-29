package com.example.web.Servlet.Impl;

import com.example.web.Bean.TDicType;
import com.example.web.Bean.TDicValue;
import com.example.web.Mapper.TDicTypeDao;
import com.example.web.Result.R;
import com.example.web.Servlet.DicServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class DicServletImpl implements DicServlet {
    @Resource
    private TDicTypeDao tDicTypeDao;
    @Override
    public R getAllType(Integer pageNum,Integer pageSize,String searchKey) {
        pageNum=(pageNum-1)*pageSize;
        if(searchKey!=""){
        //  根据条件筛选

        }
        return R.OK(tDicTypeDao.getTypeAll(pageNum,pageSize));
    }

    @Override
    public int updateTypeData(TDicType tDicType) {
        return tDicTypeDao.updateByPrimaryKey(tDicType);
    }

    @Override
    public int DeleteTypeData(Integer id) {
        return tDicTypeDao.deleteByPrimaryKey(id);
    }

    @Override
    public int addTypeData(TDicType tDicType) {
        return tDicTypeDao.insert(tDicType);
    }


}
