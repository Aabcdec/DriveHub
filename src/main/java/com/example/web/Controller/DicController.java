package com.example.web.Controller;

import com.example.web.Application;
import com.example.web.Bean.TActivity;
import com.example.web.Bean.TDicType;
import com.example.web.Bean.TDicValue;
import com.example.web.Bean.TProduct;
import com.example.web.Result.DicEnum;
import com.example.web.Result.R;
import com.example.web.Servlet.DicServlet;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RestController
public class DicController {
    @Resource
    private DicServlet dicServlet;
    @GetMapping(value = "/dicvalue/{typeCode}")
    public R dicData(@PathVariable(value = "typeCode") String typeCode) {
        if (typeCode.equals(DicEnum.ACTIVITY.getCode())) { //activity
            List<TActivity> tActivityList = (List<TActivity>)Application.cacheMap.get(typeCode);
            return R.OK(tActivityList);
        } else if (typeCode.equals(DicEnum.PRODUCT.getCode())) {
            List<TProduct> tProductList = (List<TProduct>)Application.cacheMap.get(typeCode);
            return R.OK(tProductList);
        } else {
            List<TDicValue> tDicValueList = (List<TDicValue>)Application.cacheMap.get(typeCode);
            return R.OK(tDicValueList);
        }
    }
    @GetMapping("dictionary/types")
    public R dicTypeData(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize ,
                         @RequestParam("search") String searchKey){

       return dicServlet.getAllType(pageNum,pageSize,searchKey);
    }
    @PutMapping("/dictionary/types")
    public int updateTypeData(@RequestBody TDicType tDicType){
        return dicServlet.updateTypeData(tDicType);
    }
    @DeleteMapping("/dictionary/types/{id}")
    public int DeleteTypeData(@PathVariable("id")Integer id){
        return dicServlet.DeleteTypeData(id);
    }
    @PostMapping("/dictionary/types")
    public int addTypeData(@RequestBody TDicType tDicType){
        return dicServlet.addTypeData(tDicType);
    }

}
