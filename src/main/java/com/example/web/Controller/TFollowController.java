package com.example.web.Controller;

import com.example.web.Bean.TFollow;
import com.example.web.service.FollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TFollowController {
    @Resource
    private FollowService followService;
    @PostMapping("/saveFollow")
    int saveFollow(@RequestBody TFollow tFollow){
        return followService.saveFollow(tFollow);
    }
    @PostMapping("/updateFollow")
    int updateFollow(@RequestBody TFollow tFollow){
        return followService.updateFollow(tFollow);
    }
    @GetMapping("/byIdFollow")
    List<TFollow> byIdFollows(@RequestParam("fId") Integer fId){
        return followService.byIdFollow(fId);
    }
    @GetMapping("/deleteByIdFollow")
    int deteleByIdFollow(@RequestParam("fId") Integer fId){
       return followService.deteleByIdFollow(fId);
    }
    @GetMapping("/byDeleteIdFollow")
   int byDeleteIdFollow(@RequestParam("fId") Integer fId,@RequestParam("id") Integer id){
        return followService.byDeleteIdFollow(fId,id);
    }
}
