package com.example.web.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class SignUpDateQuery {
    private long userId;
    private int actId;
    private String actName;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signUpTime;
    private int status;
    private String fullName;

}
