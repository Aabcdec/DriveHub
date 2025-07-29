package com.example.web.Controller;

import com.example.web.Bean.TDicValue;
import com.example.web.Result.R;
import com.example.web.Servlet.DicValueServlet;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class DIcValueController {
    @Resource
    private DicValueServlet dicValueServlet;
    @GetMapping("/dictionary/values")
    public R dicValueData(@RequestParam("pageNum") Integer pageNum,
                          @RequestParam("pageSize") Integer pageSize,
                          @RequestParam("typeCode")String typeCode){
        return dicValueServlet.getValueByTyoeCode(pageNum,pageSize,typeCode);
    }
    @PostMapping("/dictionary/values")
    public int addByTypeAddValue(@RequestBody TDicValue tDicValue){
       return dicValueServlet.addByTypeAddValue(tDicValue);
    }
    @PutMapping("/dictionary/values")
    public int PutByTypeAddValue(@RequestBody TDicValue tDicValue){
        return dicValueServlet.PutByTypeAddValue(tDicValue);
    }
    @DeleteMapping("/dictionary/values/{id}")
    public int deleteByIdValue(@PathVariable("id") Integer id){
        return dicValueServlet.deleteByIdValue(id);
    }
}
