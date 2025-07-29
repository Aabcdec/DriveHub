package com.example.web.Controller;

import com.example.web.Bean.TFollow;
import com.example.web.Servlet.TFollowServlet;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TFollowController {
    @Resource
    private TFollowServlet tFollowServlet;
    @PostMapping("/saveFollow")
    int saveFollow(@RequestBody TFollow tFollow){
        return tFollowServlet.saveFollow(tFollow);
    }
    @PostMapping("/updateFollow")
    int updateFollow(@RequestBody TFollow tFollow){
        return tFollowServlet.updateFollow(tFollow);
    }
    @GetMapping("/byIdFollow")
    List<TFollow> byIdFollows(@RequestParam("fId") Integer fId){
        return tFollowServlet.byIdFollow(fId);
    }
    @GetMapping("/deleteByIdFollow")
    int deteleByIdFollow(@RequestParam("fId") Integer fId){
       return tFollowServlet.deteleByIdFollow(fId);
    }
    @GetMapping("/byDeleteIdFollow")
   int byDeleteIdFollow(@RequestParam("fId") Integer fId,@RequestParam("id") Integer id){
        return tFollowServlet.byDeleteIdFollow(fId,id);
    }
}
