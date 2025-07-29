package com.example.web.Mapper;

import com.example.web.Bean.TCustomer;
import com.example.web.query.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TCustomerDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TCustomer record);

    int insertSelective(TCustomer record);

    TCustomer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TCustomer record);

    int updateByPrimaryKey(TCustomer record);
    List<TCustomer> getCustomer();
    List<marketQuery> getMarketTypes();

    int deleteByIdCustom(@Param("id") Integer id);

    List<TCustomer> selectCustomerPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);


    List<TCustomer> selectCustomerByExcel(@Param("idList") List<String> idList);

    List<TCustomer> BysearchCustomer(searchCustomerQuery searchCustomerQuery);

    Integer selectByCount();

    int updataCustomer(@Param("id") Long id, @Param("customerForm") CustomerForm customerForm);

    List<Long> getCustomerId();


}