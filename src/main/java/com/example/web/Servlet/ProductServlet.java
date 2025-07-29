package com.example.web.Servlet;

import com.example.web.Bean.TProduct;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductServlet {
    List<TProduct> getProdcutList(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    Integer updataProducut(TProduct tProduct);

    Integer addProduct(TProduct tProduct);

    Integer updataProductState(TProduct tProduct);

    ProductStatisticsQuery ProductStatistics();

    List<TProduct> searchProduct(ProductSearchQuery productSearchQuery);
}
