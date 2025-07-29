package com.example.web.query;

import lombok.Data;

@Data
public class ProductSearchQuery {
   private String  name;
   private Integer priceMax;
   private Integer priceMin;
   private Integer state;
}
