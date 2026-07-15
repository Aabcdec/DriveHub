package com.example.web.query;

import lombok.Data;

@Data
public class SearchCustomerQuery {
    private String name;
    private String address;
    private int status;
    private int createBy;
}
