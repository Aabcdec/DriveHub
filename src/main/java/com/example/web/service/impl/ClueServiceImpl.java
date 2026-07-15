package com.example.web.service.impl;

import com.alibaba.excel.EasyExcel;
import com.example.web.Bean.TClue;
import com.example.web.Listener.UploadDataListener;
import com.example.web.Mapper.TClueDao;
import com.example.web.Mapper.TCustomerDao;
import com.example.web.RabbitMQService.NotificationService;
import com.example.web.query.BaseQuery;
import com.example.web.service.ClueService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 线索业务实现：Redis 分页缓存、分布式锁、逾期消息推送。
 */
@Slf4j
@Service
public class ClueServiceImpl implements ClueService {

    private static final String REDIS_CLUE_KEY = "clueKey";

    @Resource
    private TClueDao tClueDao;
    @Resource
    private TCustomerDao tCustomerDao;
    @Resource
    private Redisson redisson;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private NotificationService notificationService;

    @Override
    public TClue selectByPrimaryKey(Integer id) {
        return tClueDao.selectByPrimaryKey(id);
    }

    @Override
    public List<TClue> getClues(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return tClueDao.getClues(offset, pageSize, BaseQuery.builder().build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TClue> getCluesWithCache(Integer pageNum, Integer pageSize) {
        String cacheKey = REDIS_CLUE_KEY + pageNum + ":" + pageSize;
        log.debug("线索分页缓存键: {}", cacheKey);

        List<TClue> clueList = (List<TClue>) redisTemplate.opsForValue().get(cacheKey);
        if (clueList != null) {
            return clueList;
        }

        String lockKey = "lock:clue:" + pageNum + ":" + pageSize;
        RLock lock = redisson.getLock(lockKey);
        try {
            boolean lockOk = lock.tryLock(2, 10, TimeUnit.SECONDS);
            if (!lockOk) {
                return getClues(pageNum, pageSize);
            }

            clueList = (List<TClue>) redisTemplate.opsForValue().get(cacheKey);
            if (clueList != null) {
                return clueList;
            }

            List<TClue> clues = getClues(pageNum, pageSize);
            if (clues == null || clues.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, Collections.emptyList(), 5, TimeUnit.MINUTES);
            } else {
                int expireTime = 25 + new Random().nextInt(10);
                redisTemplate.opsForValue().set(cacheKey, clues, expireTime, TimeUnit.MINUTES);
            }
            return clues;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return getClues(pageNum, pageSize);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSelective(TClue tClue) {
        int i = tClueDao.insertSelective(tClue);
        if (i > 0) {
            // 与历史行为一致：新增后清空当前 Redis DB 缓存
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                connection.flushDb();
                return null;
            });
        }
        return i;
    }

    @Override
    public int updateByPrimaryKeySelective(TClue tClue) {
        int result = tClueDao.updateByPrimaryKeySelective(tClue);
        if (result > 0) {
            redisTemplate.delete(REDIS_CLUE_KEY + tClue.getCurrentPage() + ":10");
        }
        return result;
    }

    @Override
    public List<TClue> selectByIdAndDateRange(TClue tClue) {
        return tClueDao.selectByIdAndDateRange(tClue);
    }

    @Override
    public void importExcel(InputStream inputStream, int create) {
        EasyExcel.read(inputStream, TClue.class, new UploadDataListener(tClueDao, create))
                .sheet()
                .doRead();
    }

    @Override
    public int deteleByIdClue(Integer id) {
        return tClueDao.updateDelete(id);
    }

    @Override
    public int deteleByIdClue(Integer id, Integer currentPage) {
        int result = deteleByIdClue(id);
        if (result > 0) {
            redisTemplate.delete(REDIS_CLUE_KEY + currentPage + ":10");
        }
        return result;
    }

    @Override
    public int updataHeader(Integer fid) {
        return tClueDao.updataHeader(fid);
    }

    @Override
    public List<TClue> overdueClueList(Integer ownerId) {
        return tClueDao.overdueClueList(ownerId);
    }

    @Override
    public String publishOverdueClues(Integer ownerId) {
        List<TClue> tClues = overdueClueList(ownerId);
        notificationService.clearUserQueue(ownerId);
        tClues.forEach(item -> notificationService.sendMessage(ownerId, item));
        return "用户" + ownerId + "管理的逾期数据已全部发完rabbitMQ中";
    }
}
