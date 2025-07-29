package com.example.web.Servlet.Impl;

import com.example.web.Mapper.TCustomerDao;
import com.example.web.Servlet.TCustomerServlet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TCustomerServletImpl implements TCustomerServlet {
    @Resource
    private TCustomerDao tCustomerDao;
    @Override
    public int deleteByIdCustom(Integer clueId) {
        return tCustomerDao.deleteByIdCustom(clueId);
    }
}
