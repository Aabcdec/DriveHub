package com.example.web.Controller;

import com.example.web.Bean.TProduct;
import com.example.web.Servlet.ProductServlet;
import com.example.web.query.ProductSearchQuery;
import com.example.web.query.ProductStatisticsQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ProductController {
    @Resource
    private ProductServlet ProductServlet;
    @GetMapping("/products")
    public List<TProduct> getProdcutList(@RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize")Integer pageSize){
        return ProductServlet.getProdcutList(pageNum,pageSize);
    }
    @PutMapping("/products/update")
    public Integer updataProducut(@RequestBody TProduct tProduct){
        return ProductServlet.updataProducut(tProduct);
    }
    @PostMapping("/products/add")
    public Integer addProduct(@RequestBody TProduct tProduct){
        return ProductServlet.addProduct(tProduct);
    }
    @PutMapping("/products/updateState")
    public Integer updataProductState(@RequestBody TProduct tProduct){
        return ProductServlet.updataProductState(tProduct);
    }
    @GetMapping("/products/statistics")
    public ProductStatisticsQuery ProductStatistics(){
        return ProductServlet.ProductStatistics();
    }
    @PostMapping("/products/search")
    public List<TProduct> searchProduct(@RequestBody ProductSearchQuery productSearchQuery){
        return ProductServlet.searchProduct(productSearchQuery);
    }

}
