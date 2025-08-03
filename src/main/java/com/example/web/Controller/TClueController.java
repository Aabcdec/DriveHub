package com.example.web.Controller;

import com.example.web.Bean.TClue;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
    @GetMapping("/threads")
    public List<TClue> getClues(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        // 1. 尝试获取分布式锁（设置超时时间）
        RLock lock = redisson.getLock(REDIS_CLUE_LOCK);
        String cacheKey = String.format(REDIS_CLUE_Key+"%d:%d", pageNum, pageSize);
        System.out.println(cacheKey);
        try {
            boolean lockOk = lock.tryLock(10, 30, TimeUnit.SECONDS); // 等待10秒，锁30秒后自动释放
            if (!lockOk) {
                return new ArrayList<>(); // 或抛异常提示系统繁忙
            }
            // 2. 查询缓存
            List<TClue> clueList = (List<TClue>) redisTemplate.opsForValue().get(cacheKey);
            if (clueList != null) {
                return clueList; // 缓存命中直接返回
            }
            // 3. 缓存未命中，查询数据库
            List<TClue> clues = tClueServlet.getClues(pageNum, pageSize);
            // 4. 写入缓存（即使空结果也缓存，避免穿透）
            redisTemplate.opsForValue().set(
                    cacheKey,
                    clues,
                    30, TimeUnit.MINUTES // 设置过期时间
            );
            return clues;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁失败", e);
        } finally {
            // 5. 确保当前线程持有锁再释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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


