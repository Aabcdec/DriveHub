package com.example.web.query;

import lombok.Data;

@Data
public class ProductStatisticsQuery {
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer disabledProducts;
    private Integer averagePrice;
}
