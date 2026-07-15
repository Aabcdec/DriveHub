package com.example.web.service.impl;

import com.example.web.Bean.TDicType;
import com.example.web.Mapper.TDicTypeDao;
import com.example.web.service.DicTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class DicTypeServiceImpl implements DicTypeService {
    @Resource
    private TDicTypeDao tDicTypeDao;
    @Override
    public List<TDicType> getAll() {
        return tDicTypeDao.getAll();
    }
}
