package com.example.web.Servlet;

import com.example.web.Bean.TCustomer;
import com.example.web.query.*;

import java.util.List;

public interface TCudtomerServlet {
    List<TCustomer> getCustomer();
    List<marketQuery>getMarketTypes();



    int SaveCustomr(TCustomer tCustomer, int createBy);

    List<TCustomer> selectCustomerPage(Integer pageNum, Integer pageSize);

    List<CustomerExcel> getCustomerByExcel(List<String> idList);

    List<TCustomer> BysearchCustomer(searchCustomerQuery searchCustomerQuery);

    int updataCustomer(Long id, CustomerForm customerForm);

    List<Long> getCustomerId();


}
