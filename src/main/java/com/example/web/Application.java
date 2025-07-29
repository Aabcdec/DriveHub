package com.example.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@MapperScan({"com.example.web.Mapper", "com.example.web.Bean"})
@EnableScheduling
@EnableCaching
public class Application {
    public static final Map<String, Object> cacheMap = new HashMap<>();
    //用于缓存Excel转换字段
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
