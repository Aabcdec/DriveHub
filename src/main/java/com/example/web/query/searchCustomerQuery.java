package com.example.web.query;

import lombok.Data;

@Data
public class searchCustomerQuery {
    private String name;
    private String address;
    private int status;
    private int manager;
}
