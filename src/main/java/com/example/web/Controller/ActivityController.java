package com.example.web.Controller;

import com.example.web.Bean.TActivity;
import com.example.web.Bean.TClue;
import com.example.web.Servlet.ActivityServlet;
import com.example.web.query.ActiveProductQuery;
import com.example.web.query.IdListRequest;
import com.example.web.query.myUpSignUpDataQuery;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class ActivityController {
    @Resource
    private ActivityServlet activityServlet;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private Redisson redisson;
    private static  final  String REDIS_ACTIVE_LOCK="redisActiveLock";
    private static  final  String REDIS_ACTIVE_Key="activeKey";
    //分页
    @GetMapping("/market/campaigns")
    public List<TActivity> campaigns(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        // 1. 尝试获取分布式锁（设置超时时间）
        RLock lock = redisson.getLock(REDIS_ACTIVE_LOCK);
        String cacheKey = String.format(REDIS_ACTIVE_Key+"%d:%d", pageNum, pageSize);
        try {
            boolean lockOk = lock.tryLock(10, 30, TimeUnit.SECONDS); // 等待10秒，锁30秒后自动释放
            if (!lockOk) {
                return new ArrayList<>(); // 或抛异常提示系统繁忙
            }
            // 2. 查询缓存
            List<TActivity> activeList = (List<TActivity>) redisTemplate.opsForValue().get(cacheKey);
            if (activeList != null) {
                return activeList; // 缓存命中直接返回
            }
            // 3. 缓存未命中，查询数据库
            List<TActivity> actives = activityServlet.getActs(pageNum,pageSize);
            // 4. 写入缓存（即使空结果也缓存，避免穿透）
            redisTemplate.opsForValue().set(
                    cacheKey,
                    actives,
                    30, TimeUnit.MINUTES // 设置过期时间
            );
            return actives;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁失败", e);
        } finally {
            // 5. 确保当前线程持有锁再释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
//        return activityServlet.getActs(pageNum,pageSize); //正常无缓存的情况
    }
    @GetMapping("/updateActProduct/{id}")
    public int updateActProduct(@PathVariable("id")Integer id,@RequestParam("productId")Integer productId){
        return activityServlet.updateActProduct(id,productId);
    }
    @GetMapping("/getActAll")
    public List<TActivity> getActAll(){
        return activityServlet.getActAll();
    }
    @PostMapping("/market/campaigns")
    public int updateActivityById(@RequestBody TActivity tActivity){
        int i= activityServlet.updateByPrimaryKeySelective(tActivity);
        if(i>0){
            System.out.println("前端传来的页码值"+tActivity.getCurrentPage());
            redisTemplate.delete(REDIS_ACTIVE_Key+tActivity.getCurrentPage()+":10");
        }
//        return activityServlet.updateByPrimaryKeySelective(tActivity);
        return i;
    }
    @GetMapping("/market/delCampaignsById")
    public int updateActiverByIdAndCurrentPage(@RequestParam("id")Integer id,@RequestParam("currentPage")Integer currentPage){
        // 1. 删除数据库数据 这里采用软删除
        int result = activityServlet.updateByIdActive(id);
        if (result > 0) {
//            // 2. 删除该数据的独立缓存
//            redisTemplate.delete("clue:" + id);
            // 3. 清理可能受影响的分页缓存（如第1页）
            System.out.println(REDIS_ACTIVE_Key);
            redisTemplate.delete(REDIS_ACTIVE_Key+currentPage+":10");
        }
        return result;
    }
    @PostMapping("/market/addCampaigns")
    public int addActivity(@RequestBody TActivity tActivity){
        System.out.println(tActivity);
        int i=  activityServlet.addTActivity(tActivity);
        if(i>0){
            //情空缓存 应该按照日期降序删第一页缓存
            redisTemplate.delete(REDIS_ACTIVE_Key+1+":10");
//            redisTemplate.execute((RedisCallback<Object>) connection -> {
//                connection.flushDb();
//                return null;
//            });
        }
//        return  activityServlet.addTActivity(tActivity);
        return i;
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
    public ActiveProductQuery selectByPrimaryKey(@RequestParam("id") int id){
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
    @GetMapping("/deleteParty")
    public Integer deleteParty(@RequestParam("id") Integer id){
        return activityServlet.deleteParty(id);
    }
}
