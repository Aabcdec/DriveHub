package com.example.web.Controller;

import com.alibaba.excel.EasyExcel;
import com.example.web.Bean.TCustomer;
import com.example.web.service.CustomerService;
import com.example.web.query.*;
import com.example.web.util.TokenUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 客户相关 REST 接口。路径含历史拼写 {@code /sreachCustomerPage}，不可改动。
 */
@RestController
public class CustomerController {
    private static  final  String REDIS_CLUE_Key="clueKey";
    @Resource
    private CustomerService customerService;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private RedisTemplate redisTemplate;
    @GetMapping("/getCustomer")
    public List<TCustomer> getAllCustomer(){
        return customerService.getCustomer();
    }
    @GetMapping("/getMarketTypes")
    public List<MarketQuery>getMarketTypes(){
        return customerService.getMarketTypes();
    }
    @PostMapping("/convertCustomer/{pageNum}")
    public int convertCustomer(@PathVariable("pageNum") Integer pageNum,@RequestBody TCustomer tCustomer, HttpServletRequest request){
       //这里很简单拿到当前线索id拼接删除缓存中的页面即可！！！
        String userAgent = request.getHeader("Authorization");
        TokenWrapper tokenWrapper = tokenUtil.parseToken(userAgent);
        int createBy=tokenWrapper.getValue().getId();
        TCustomer tCustomer1=new TCustomer();
        tCustomer1.setClueId(tCustomer.getClueId());
        tCustomer1.setProduct(tCustomer.getProduct());
        tCustomer1.setDescription(tCustomer.getDescription());
        tCustomer1.setNextContactTime(tCustomer.getNextContactTime());
        tCustomer1.setCreateTime(new Date());
        tCustomer1.setCreateBy(tokenWrapper.getValue().getId());
        int i = customerService.SaveCustomr(tCustomer1, createBy);
        if(i>0){
            redisTemplate.delete(REDIS_CLUE_Key+pageNum+":10");
        }
        return i;
    }
    @GetMapping("/selectCustomerPage")
    public List<TCustomer> selectCustomerPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        return customerService.selectCustomerPage(pageNum,pageSize);
    }
    //跟进条件筛选客户
    @PostMapping("/sreachCustomerPage")
    public List<TCustomer> BysearchCustomer(@RequestBody SearchCustomerQuery SearchCustomerQuery){
        return customerService.BysearchCustomer(SearchCustomerQuery);
    }
    //跟新客户信息
    @PutMapping("/clients/{id}")
    public int updataCustomer(@PathVariable Long id,
                              @RequestBody CustomerForm customerForm){
        return customerService.updataCustomer(id,customerForm);
    }
    @GetMapping("/getCustomerIds")
    public List<Long> getCustomerId(){
      return  customerService.getCustomerId();
    }

    @GetMapping(value = "/exportExcel")
    public void exportExcel(HttpServletResponse response,
                            @RequestParam(value = "ids", required = false) String ids) throws IOException {
        //要想让浏览器弹出下载框，你后端要设置一下响应头信息
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("客户信息数据"+System.currentTimeMillis(), String.valueOf(StandardCharsets.UTF_8)) + ".xlsx");
        //2、后端查询数据库的数据，把数据写入Excel，然后把Excel以IO流的方式输出到前端浏览器（我们来实现）
        List<String> idList = StringUtils.hasText(ids) ? Arrays.asList(ids.split(",")) : new ArrayList<>();
        System.out.println(idList);
        List<CustomerExcel> dataList = customerService.getCustomerByExcel(idList);
        EasyExcel.write(response.getOutputStream(), CustomerExcel.class)
                .sheet()
                .doWrite(dataList);
    }
}
