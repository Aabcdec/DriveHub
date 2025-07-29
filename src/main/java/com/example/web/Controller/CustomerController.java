package com.example.web.Controller;

import com.alibaba.excel.EasyExcel;
import com.example.web.Bean.TCustomer;
import com.example.web.Servlet.TCudtomerServlet;
import com.example.web.query.*;
import com.example.web.util.TokenUtil;
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

@RestController
public class CustomerController {
    @Resource
    private TCudtomerServlet tCudtomerServlet;
    @Resource
    private TokenUtil tokenUtil;
    @GetMapping("/getCustomer")
    public List<TCustomer> getAllCustomer(){
        return tCudtomerServlet.getCustomer();
    }
    @GetMapping("/getMarketTypes")
    public List<marketQuery>getMarketTypes(){
        return tCudtomerServlet.getMarketTypes();
    }
    @PostMapping("/convertCustomer")
    public int convertCustomer(@RequestBody TCustomer tCustomer, HttpServletRequest request){
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
        return tCudtomerServlet.SaveCustomr(tCustomer1,createBy);
    }
    @GetMapping("/selectCustomerPage")
    public List<TCustomer> selectCustomerPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        return tCudtomerServlet.selectCustomerPage(pageNum,pageSize);
    }
    //跟进条件筛选客户
    @PostMapping("/sreachCustomerPage")
    public List<TCustomer> BysearchCustomer(@RequestBody searchCustomerQuery searchCustomerQuery){
        return tCudtomerServlet.BysearchCustomer(searchCustomerQuery);
    }
    //跟新客户信息
    @PutMapping("/clients/{id}")
    public int updataCustomer(@PathVariable Long id,
                              @RequestBody CustomerForm customerForm){
        return tCudtomerServlet.updataCustomer(id,customerForm);
    }
    @GetMapping("/getCustomerIds")
    public List<Long> getCustomerId(){
      return  tCudtomerServlet.getCustomerId();
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
        List<CustomerExcel> dataList = tCudtomerServlet.getCustomerByExcel(idList);

        EasyExcel.write(response.getOutputStream(), CustomerExcel.class)
                .sheet()
                .doWrite(dataList);
    }
}
