package com.example.web.service;

import com.example.web.Bean.TCustomer;
import com.example.web.query.*;

import java.util.List;

public interface CustomerService {
    List<TCustomer> getCustomer();
    List<MarketQuery>getMarketTypes();



    int SaveCustomr(TCustomer tCustomer, int createBy);

    List<TCustomer> selectCustomerPage(Integer pageNum, Integer pageSize);

    List<CustomerExcel> getCustomerByExcel(List<String> idList);

    List<TCustomer> BysearchCustomer(SearchCustomerQuery searchCustomerQuery);

    int updataCustomer(Long id, CustomerForm customerForm);

    List<Long> getCustomerId();


}
