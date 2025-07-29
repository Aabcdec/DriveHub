package com.example.web.task;


import com.example.web.Application;
import com.example.web.Bean.TActivity;
import com.example.web.Bean.TDicType;
import com.example.web.Bean.TDicValue;
import com.example.web.Bean.TProduct;
import com.example.web.Result.DicEnum;
import com.example.web.Servlet.DicTypeServlet;
import com.example.web.Servlet.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling //开启定时任务
@Component
public class DataTask {

    @Resource
    private DicTypeServlet dicTypeServlet;
//    @Resource
//    private ActivityService activityService;
    @Resource
    private ProductService productService;
//    @Scheduled(cron = "${project.task.cron}")
@Scheduled(fixedDelayString = "${project.task.delay}", zone = "Asia/Shanghai", timeUnit = TimeUnit.MILLISECONDS, initialDelay  = 1000)
    public void task(){
        List<TDicType> dicTypes = dicTypeServlet.getAll();
        dicTypes.forEach(tDicType -> {

            String typeCode = tDicType.getTypeCode();
            List<TDicValue> tDicValueList = tDicType.getDicValueList();
            System.out.println(typeCode+":"+tDicValueList);
            Application.cacheMap.put(typeCode, tDicValueList);
        });

        //查询所有在售产品
        List<TProduct> tProductList = productService.getAllOnSaleProduct();
        Application.cacheMap.put(DicEnum.PRODUCT.getCode(), tProductList);
//
//        //查询所有正在进行的市场活动
//        List<TActivity> tActivityList = activityService.getOngoingActivity();
//        Application.cacheMap.put(DicEnum.ACTIVITY.getCode(), tActivityList);
    }
}
