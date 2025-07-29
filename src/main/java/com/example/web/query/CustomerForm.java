package com.example.web.query;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerForm {
    private String address;
    private String email;
    private String createBy;
    private String name;
    private String phone;
    private String status;
    private String type;
    private Date editTime =new Date();
    private Integer clueId;
}
