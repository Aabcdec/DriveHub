package com.example.web;

import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

/**
 * BeProject 应用启动入口。
 *
 * <p>启用能力：MyBatis 扫描、定时任务、Spring Cache。
 * Redisson 用于线索列表等场景的分布式锁。
 */
@SpringBootApplication
@MapperScan("com.example.web.Mapper")
@EnableScheduling
@EnableCaching
public class Application {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private Integer redisPort;

    /** Excel 导入时字典字段的内存缓存，由 {@link com.example.web.task.DataTask} 定时刷新 */
    public static final Map<String, Object> cacheMap = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /** 配置 Redisson 客户端，供分布式锁（如线索分页缓存）使用 */
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
