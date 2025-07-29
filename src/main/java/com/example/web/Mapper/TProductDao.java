package com.example.web.Mapper;

import com.example.web.Bean.TProduct;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TProductDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TProduct record);

    int insertSelective(TProduct record);

    TProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TProduct record);

    int updateByPrimaryKey(TProduct record);

    List<TProduct> getAllOnSaleProduct();

    List<TProduct> getProdcutList(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    Integer updataProductState(@Param("id") Integer id, @Param("state") Integer state);

    ProductStatisticsQuery ProductStatistics();

    List<TProduct> searchProduct(ProductSearchQuery productSearchQuery);
}