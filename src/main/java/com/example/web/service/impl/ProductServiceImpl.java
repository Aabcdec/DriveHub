package com.example.web.service.impl;

import com.example.web.Bean.TProduct;
import com.example.web.Mapper.TProductDao;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import com.example.web.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品业务实现（合并原 ProductServlet 与独立 ProductService）。
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private TProductDao tProductDao;

    @Override
    public List<TProduct> getProdcutList(Integer pageNum, Integer pageSize) {
        return tProductDao.getProdcutList(pageNum, pageSize);
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

    @Override
    public List<TProduct> getAllOnSaleProduct() {
        return tProductDao.getAllOnSaleProduct();
    }
}
