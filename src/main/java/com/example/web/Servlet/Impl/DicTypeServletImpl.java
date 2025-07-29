package com.example.web.Servlet.Impl;

import com.example.web.Bean.TDicType;
import com.example.web.Mapper.TDicTypeDao;
import com.example.web.Servlet.DicTypeServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class DicTypeServletImpl implements DicTypeServlet {
    @Resource
    private TDicTypeDao tDicTypeDao;
    @Override
    public List<TDicType> getAll() {
        return tDicTypeDao.getAll();
    }
}
