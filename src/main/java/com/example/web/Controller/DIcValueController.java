package com.example.web.Controller;

import com.example.web.Bean.TDicValue;
import com.example.web.Result.R;
import com.example.web.service.DicValueService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 字典值接口。HTTP 路径保持不变以兼容前端。
 */
@RestController
public class DicValueController {
    @Resource
    private DicValueService dicValueService;

    @GetMapping("/dictionary/values")
    public R dicValueData(@RequestParam("pageNum") Integer pageNum,
                          @RequestParam("pageSize") Integer pageSize,
                          @RequestParam("typeCode") String typeCode) {
        return dicValueService.getValueByTyoeCode(pageNum, pageSize, typeCode);
    }

    @PostMapping("/dictionary/values")
    public int addByTypeAddValue(@RequestBody TDicValue tDicValue) {
        return dicValueService.addByTypeAddValue(tDicValue);
    }

    @PutMapping("/dictionary/values")
    public int PutByTypeAddValue(@RequestBody TDicValue tDicValue) {
        return dicValueService.PutByTypeAddValue(tDicValue);
    }

    @DeleteMapping("/dictionary/values/{id}")
    public int deleteByIdValue(@PathVariable("id") Integer id) {
        return dicValueService.deleteByIdValue(id);
    }
}
