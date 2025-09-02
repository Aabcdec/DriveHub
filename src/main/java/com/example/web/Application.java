package com.example.web;


import com.example.web.RabbitMQService.NotificationService;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
@SpringBootApplication
@MapperScan({"com.example.web.Mapper", "com.example.web.Bean"})
@EnableScheduling
@EnableCaching
public class Application {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.host.port}")
    private Integer redisPost;
    @Resource
    private NotificationService notificationService;
    public static final Map<String, Object> cacheMap = new HashMap<>();
    //用于缓存Excel转换字段
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
    @Bean
    public Redisson redisson(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPost)
                .setDatabase(0);
        return (Redisson) Redisson.create(config);

    }


}
