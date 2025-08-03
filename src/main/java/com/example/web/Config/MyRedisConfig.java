package com.example.web.Config;

import com.example.web.Bean.TActivity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class MyRedisConfig {
//        @Bean
//        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//            // 使用 Jackson2JsonRedisSerializer 序列化值
//            Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//
//            // 配置缓存默认设置
//            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
//                    .entryTtl(Duration.ofMinutes(10));  // 全局缓存过期时间
//
//            return RedisCacheManager.builder(connectionFactory)
//                    .cacheDefaults(config)
//                    .build();
//        }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用 StringRedisSerializer 序列化 Key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 使用 Jackson2JsonRedisSerializer 序列化 Value
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
    }

