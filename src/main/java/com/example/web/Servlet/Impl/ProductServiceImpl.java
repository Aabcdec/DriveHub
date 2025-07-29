package com.example.web.Servlet.Impl;

import com.example.web.Bean.TProduct;
import com.example.web.Mapper.TProductDao;
import com.example.web.Servlet.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private TProductDao tProductDao;
    @Override
    public List<TProduct> getAllOnSaleProduct() {
        return tProductDao.getAllOnSaleProduct();
    }
}
