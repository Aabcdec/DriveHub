package com.example.web.Controller;

import com.example.web.Bean.TTranRemark;
import com.example.web.service.TranRemarkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.swing.plaf.PanelUI;
import java.util.List;

@RestController
public class TranRemarkController {
    @Resource
    private TranRemarkService tranRemarkService;
    @PostMapping("/transactions/remarks/add")
    public int saveRemark(@RequestBody TTranRemark remark){
        return  tranRemarkService.saveRemark(remark);
    }
    @GetMapping("/transactions/remarks")
    public List<TTranRemark> getByIdList(@RequestParam("tranId")Integer tranId){
        return tranRemarkService.getByIdList(tranId);
    }
}
