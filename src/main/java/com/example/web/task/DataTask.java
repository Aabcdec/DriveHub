package com.example.web.task;

import com.example.web.Application;
import com.example.web.Bean.TDicType;
import com.example.web.Bean.TDicValue;
import com.example.web.Bean.TProduct;
import com.example.web.Result.DicEnum;
import com.example.web.service.DicTypeService;
import com.example.web.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ???????????? {@link Application#cacheMap}????????????
 */
@Slf4j
@EnableScheduling
@Component
public class DataTask {

    @Resource
    private DicTypeService dicTypeService;

    @Resource
    private ProductService productService;

    @Scheduled(fixedDelayString = "${project.task.delay}", zone = "Asia/Shanghai",
            timeUnit = TimeUnit.MILLISECONDS, initialDelay = 1000)
    public void task() {
        List<TDicType> dicTypes = dicTypeService.getAll();
        dicTypes.forEach(tDicType -> {
            String typeCode = tDicType.getTypeCode();
            List<TDicValue> tDicValueList = tDicType.getDicValueList();
            log.debug("??????: {} -> {} ?", typeCode,
                    tDicValueList == null ? 0 : tDicValueList.size());
            Application.cacheMap.put(typeCode, tDicValueList);
        });

        List<TProduct> tProductList = productService.getAllOnSaleProduct();
        Application.cacheMap.put(DicEnum.PRODUCT.getCode(), tProductList);
        log.debug("????????: {} ?", tProductList == null ? 0 : tProductList.size());
    }
}
