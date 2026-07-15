package com.example.web.Controller;

import com.example.web.Bean.TTran;
import com.example.web.Bean.TTranRemark;
import com.example.web.service.TranService;
import com.example.web.query.StatisticsQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TranController {
    @Resource
    private TranService tranService;
    @GetMapping("/getTran")
    public List<TTran> getTran(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        return tranService.getTran(pageNum,pageSize);
    }
    @PutMapping("/transactions/update")
    public int updataTran(@RequestBody TTran tTran){
    return tranService.updataTran(tTran);
    }
    @PostMapping("/transactions/add")
    public int addTran(@RequestBody TTran tTran){
        return tranService.addTran(tTran);
    }
    @GetMapping("/transactions/statistics")
    public StatisticsQuery getStatistics(){
        return  tranService.getStatistics();
    }
    @PostMapping("/transactions/search")
    public List<TTran> searchTran(@RequestBody TTran tTran){
        System.out.println(tTran.toString());
        return tranService.searchTran(tTran);
    }

}
