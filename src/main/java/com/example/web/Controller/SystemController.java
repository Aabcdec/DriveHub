package com.example.web.Controller;

import com.example.web.Bean.TSystemInfo;
import com.example.web.service.SystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SystemController {
    @Resource
    private SystemService systemService;
    @GetMapping("/systemInfo")
    public List<TSystemInfo> getSystemInfos(){
        return systemService.getSystemInfos();
    }
    @PostMapping("/systemInfo/update")
    public int updateSystemInfo(@RequestBody TSystemInfo tSystemInfo){
        return systemService.updateSystemInfo(tSystemInfo);
    }
}
