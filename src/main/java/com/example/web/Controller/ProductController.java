package com.example.web.Controller;

import com.example.web.Bean.TProduct;
import com.example.web.service.ProductService;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品 REST 接口。路径前缀 {@code /products*}，响应为裸数组/数值。
 */
@RestController
public class ProductController {
    @Resource
    private ProductService productService;
    @GetMapping("/products")
    public List<TProduct> getProdcutList(@RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize")Integer pageSize){
        return productService.getProdcutList(pageNum,pageSize);
    }
    @PutMapping("/products/update")
    public Integer updataProducut(@RequestBody TProduct tProduct){
        return productService.updataProducut(tProduct);
    }
    @PostMapping("/products/add")
    public Integer addProduct(@RequestBody TProduct tProduct){
        return productService.addProduct(tProduct);
    }
    @PutMapping("/products/updateState")
    public Integer updataProductState(@RequestBody TProduct tProduct){
        return productService.updataProductState(tProduct);
    }
    @GetMapping("/products/statistics")
    public ProductStatisticsQuery ProductStatistics(){
        return productService.ProductStatistics();
    }
    @PostMapping("/products/search")
    public List<TProduct> searchProduct(@RequestBody ProductSearchQuery productSearchQuery){
        return productService.searchProduct(productSearchQuery);
    }

}
