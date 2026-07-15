package com.example.web.query;

import lombok.Data;

@Data
public class SignUpActiveProductQuery {
    private Integer ownerId;
    private String actName;
    private String description;
    private Integer intentionProduct;
}
