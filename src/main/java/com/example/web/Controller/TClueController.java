package com.example.web.Controller;

import com.example.web.Bean.TClue;
import com.example.web.RabbitMQService.NotificationService;
import com.example.web.Servlet.TClueServlet;
import com.example.web.query.TokenWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class TClueController {
    @Resource
    private TClueServlet tClueServlet;
    @Resource
    private Redisson redisson;
    @Resource
    private RedisTemplate redisTemplate;
    private static  final  String REDIS_CLUE_LOCK="redisClueLock";
    private static  final  String REDIS_CLUE_Key="clueKey";
    //修改跟进
    @GetMapping("/updataHeader")
    public int updataHeader(@RequestParam("fid") Integer fid){
        return tClueServlet.updataHeader(fid);
    }
    @GetMapping("/threads")
    public List<TClue> getClues(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        List<TClue> clues = tClueServlet.getClues(pageNum, pageSize);
        return clues;
//
//        // 确保缓存键唯一性
//        String cacheKey = REDIS_CLUE_Key + pageNum + ":" + pageSize;
//        System.out.println("Cache Key: " + cacheKey);
//
//        // 2. 先查询缓存（不需要加锁）
//        List<TClue> clueList = (List<TClue>) redisTemplate.opsForValue().get(cacheKey);
//        if (clueList != null) {
//            return clueList;
//        }
//
//        // 为每个分页创建独立的锁，避免锁竞争
//        String lockKey = "lock:clue:" + pageNum + ":" + pageSize;
//        RLock lock = redisson.getLock(lockKey);
//
//        try {
//            // 设置合理的锁超时时间
//            boolean lockOk = lock.tryLock(2, 10, TimeUnit.SECONDS);
//            if (!lockOk) {
//                // 获取锁失败时，可以返回空或尝试其他策略
//                return tClueServlet.getClues(pageNum, pageSize);
//            }
//
//            // 再次检查缓存（Double Check）
//            clueList = (List<TClue>) redisTemplate.opsForValue().get(cacheKey);
//            if (clueList != null) {
//                return clueList;
//            }
//
//            // 查询数据库
//            List<TClue> clues = tClueServlet.getClues(pageNum, pageSize);
//            // 在查询数据库后
//            if (clues == null || clues.isEmpty()) {
//                // 缓存空值，设置较短过期时间
//                redisTemplate.opsForValue().set(
//                        cacheKey,
//                        Collections.emptyList(),
//                        5, TimeUnit.MINUTES // 空结果缓存5分钟
//                );
//            } else {
//                // 正常缓存数据
//                Random random = new Random();
//                int expireTime = 25 + random.nextInt(10);
//                redisTemplate.opsForValue().set(
//                        cacheKey,
//                        clues,
//                        expireTime, TimeUnit.MINUTES
//                );
//            }
//            // 写入缓存，设置随机过期时间避免雪崩
//            Random random = new Random();
//            int expireTime = 25 + random.nextInt(10); // 25-35分钟随机
//            redisTemplate.opsForValue().set(
//                    cacheKey,
//                    clues,
//                    expireTime, TimeUnit.MINUTES
//            );
//
//            return clues;
//
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return tClueServlet.getClues(pageNum, pageSize); // 降级策略
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//       }
    }
    @PostMapping("/AddThreads")
    public int insertSelective(@RequestBody TClue tClue){
        System.out.println(tClue.toString());
       int i=tClueServlet.insertSelective(tClue);
       if(i>0){
           //情空缓存
           redisTemplate.execute((RedisCallback<Object>) connection -> {
               connection.flushDb();
               return null;
           });
       }
        return i;
    }

    //预期接口 这里要区分人
    @Resource
    private NotificationService notificationService;
    @GetMapping("/overdueClueList")
    public String overdueClueList(@RequestParam("ownerId") Integer ownerId) {

        // 只调用一次服务方法
        List<TClue> tClues = tClueServlet.overdueClueList(ownerId);
        //实现清除指定队列
        notificationService.clearUserQueue(ownerId);
        // 存入RabbitMQ
        tClues.forEach(item -> {
            notificationService.sendMessage(ownerId, item);
        });
        return "用户"+ownerId+"管理的逾期数据已全部发完rabbitMQ中";
    }
    @PostMapping("/upThreads")
    public int updateByPrimaryKeySelective(@RequestBody TClue tClue) {
        // 1. 更新数据库
        int result = tClueServlet.updateByPrimaryKeySelective(tClue);
//        int currentPage=tClue.getCurrentPage();
        System.out.println(tClue.toString());
        if (result > 0) {
//            // 2. 删除该数据关联的缓存（精准清理）
//            String cacheKey = "clue:" + tClue.getId(); // 假设按ID缓存
//            redisTemplate.delete(cacheKey);
            // 如果缓存了分页数据，可选择性清理（如只清理可能受影响的分页）
            redisTemplate.delete(REDIS_CLUE_Key+tClue.getCurrentPage()+":10");
        }
        return result;
    }
    @GetMapping("/deleteByIdClue")
    public int deteleByIdClue(@RequestParam("id") Integer id,@RequestParam("currentPage") Integer currentPage){
        // 1. 删除数据库数据
        int result = tClueServlet.deteleByIdClue(id);
        if (result > 0) {
//            // 2. 删除该数据的独立缓存
//            redisTemplate.delete("clue:" + id);
            // 3. 清理可能受影响的分页缓存（如第1页）
            System.out.println(REDIS_CLUE_Key);
            redisTemplate.delete(REDIS_CLUE_Key+currentPage+":10");
        }
        return result;
    }
    @PostMapping("/searchThread")
    public List<TClue> searchThreads(@RequestBody TClue tClue){
        System.out.println(tClue.toString());
        return tClueServlet.selectByIdAndDateRange(tClue);
    }
    @PostMapping(value = "/importExcel")
    public int importExcel(MultipartFile file, HttpServletRequest request) throws IOException { //filex的名字要和前端formData里面的名字相同，否则接收不到
        // 1. 获取单个请求头
        String userAgent = request.getHeader("Authorization");
        int CreateId=1;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TokenWrapper response = objectMapper.readValue(userAgent, TokenWrapper.class);

            // 访问转换后的对象
            System.out.println("ID: " + response.getValue().getId());
            System.out.println("Token: " + response.getValue().getToken());
            CreateId=response.getValue().getId();
            System.out.println("Name: " + response.getValue().getName());
            System.out.println("Expire Time: " + response.getExpireTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tClueServlet.importExcel(file.getInputStream(),CreateId);

        return 1;
    }
}


