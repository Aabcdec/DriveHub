package com.example.web.Servlet.Impl;

import com.example.web.Bean.TProduct;
import com.example.web.Mapper.TProductDao;
import com.example.web.Servlet.ProductServlet;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ProductServletImpl implements ProductServlet {
    @Resource
    private TProductDao tProductDao;
    @Override
    public List<TProduct> getProdcutList(Integer pageNum, Integer pageSize) {
        return tProductDao.getProdcutList(pageNum,pageSize);
    }

    @Override
    public Integer updataProducut(TProduct tProduct) {
        return tProductDao.updateByPrimaryKeySelective(tProduct);
    }

    @Override
    public Integer addProduct(TProduct tProduct) {
        return tProductDao.insertSelective(tProduct);
    }

    @Override
    public Integer updataProductState(TProduct tProduct) {
        return tProductDao.updateByPrimaryKeySelective(tProduct);
    }

    @Override
    public ProductStatisticsQuery ProductStatistics() {
        return tProductDao.ProductStatistics();
    }

    @Override
    public List<TProduct> searchProduct(ProductSearchQuery productSearchQuery) {
        return tProductDao.searchProduct(productSearchQuery);
    }
}
