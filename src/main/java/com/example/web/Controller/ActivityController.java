package com.example.web.Controller;

import com.example.web.Bean.TActivity;
import com.example.web.Servlet.ActivityServlet;
import com.example.web.query.IdListRequest;
import com.example.web.query.myUpSignUpDataQuery;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
public class ActivityController {
    @Resource
    private ActivityServlet activityServlet;
    //分页
    @GetMapping("/market/campaigns")
    public List<TActivity> campaigns(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
       return activityServlet.getActs(pageNum,pageSize);
    }
    @GetMapping("/getActAll")
    public List<TActivity> getActAll(){
        return activityServlet.getActAll();
    }
    @PostMapping("/market/campaigns")
    public int updateActivityById(@RequestBody TActivity tActivity){
        return activityServlet.updateByPrimaryKeySelective(tActivity);
    }
    @PostMapping("/market/addCampaigns")
    public int addActivity(@RequestBody TActivity tActivity){
        System.out.println(tActivity);
        return  activityServlet.addTActivity(tActivity);
    }
    @GetMapping("/market/delCampaigns")
    public int deleteById(@RequestParam("id") int id){
        System.out.println(id);
        return activityServlet.deleteById(id);
    }
    @PostMapping("/selectByIdAndDateRange")
    public List<TActivity> selectByIdAndDateRange(@RequestBody TActivity tActivity){
        System.out.println(tActivity.toString());
        return activityServlet.queryByIdAndDate(tActivity.getId(),tActivity.getStartTime(),tActivity.getEndTime());
    }
    //获取最近活动
    @PostMapping("/selectByIdsAct")
    public List<TActivity>selectByIdsAct(@RequestBody IdListRequest ids){
        return activityServlet.selectByIdsAct(ids.getIds());
    }
    //获取用户报名信息
    @PostMapping("/selectByIdsSignUpData")
    public List<TActivity>selectByIdsSignUpData(@RequestBody myUpSignUpDataQuery myUpSignUpDataQuery){
        return activityServlet.selectByIdsSignUpData(myUpSignUpDataQuery);
    }
    @GetMapping("/getActDetail")
    public TActivity selectByPrimaryKey(@RequestParam("id") int id){
        return activityServlet.selectByPrimaryKey(id);
    }
    @PostMapping("/activity/register")
    private int increaseParticipants(@RequestBody int id){
        return activityServlet.increaseParticipants(id);
    }
    @GetMapping("/updateParty")
    public Integer updateParty(@RequestParam("id") Integer id){
        System.out.println(id);
        return activityServlet.updateParty(id);
    }
}
