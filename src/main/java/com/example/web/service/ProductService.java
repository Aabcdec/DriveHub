package com.example.web.service;

import com.example.web.Bean.TProduct;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品业务服务：CRUD / 搜索 / 统计，以及定时任务用的在售产品列表。
 */
public interface ProductService {
    List<TProduct> getProdcutList(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    Integer updataProducut(TProduct tProduct);

    Integer addProduct(TProduct tProduct);

    Integer updataProductState(TProduct tProduct);

    ProductStatisticsQuery ProductStatistics();

    List<TProduct> searchProduct(ProductSearchQuery productSearchQuery);

    /** 定时任务刷新缓存：全部上架产品 */
    List<TProduct> getAllOnSaleProduct();
}
