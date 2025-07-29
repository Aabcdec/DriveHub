package com.example.web.Controller;

import com.example.web.Bean.TTran;
import com.example.web.Bean.TTranRemark;
import com.example.web.Servlet.TranServlet;
import com.example.web.query.StatisticsQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TranController {
    @Resource
    private TranServlet tranServlet;
    @GetMapping("/getTran")
    public List<TTran> getTran(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        return tranServlet.getTran(pageNum,pageSize);
    }
    @PutMapping("/transactions/update")
    public int updataTran(@RequestBody TTran tTran){
    return tranServlet.updataTran(tTran);
    }
    @PostMapping("/transactions/add")
    public int addTran(@RequestBody TTran tTran){
        return tranServlet.addTran(tTran);
    }
    @GetMapping("/transactions/statistics")
    public StatisticsQuery getStatistics(){
        return  tranServlet.getStatistics();
    }
    @PostMapping("/transactions/search")
    public List<TTran> searchTran(@RequestBody TTran tTran){
        System.out.println(tTran.toString());
        return tranServlet.searchTran(tTran);
    }

}
