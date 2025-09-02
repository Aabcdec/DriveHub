//package com.example.web.RabbitMQService;
//
//import com.example.web.Config.RabbitMQConfig;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RabbitMQConsumer {
//        @Autowired
//        private SimpMessagingTemplate messagingTemplate; // 用于推送WebSocket消息
//
//        @Resource
//        private RedisTemplate<String, String> redisTemplate;
//
//        // 使用与配置类中相同的队列名称
//        @RabbitListener(queues = RabbitMQConfig.QUEUE_PREFIX)
//        public void handleNotificationMessage(Object message, Message amqpMessage) {
//            // 获取消息ID，用于幂等性处理
//            String messageId = amqpMessage.getMessageProperties().getMessageId();
//
//            // 检查消息是否已处理过（基于Redis实现幂等性）
//            String redisKey = "rabbitmq:processed:msg:" + messageId;
//            Boolean hasProcessed = redisTemplate.hasKey(redisKey);
//
//            if (Boolean.TRUE.equals(hasProcessed)) {
//                System.out.println("重复消息，已忽略: " + messageId);
//                return;
//            }
//
//            // 处理消息
//            System.out.println("Received message: " + message);
//
//            // 通过WebSocket将消息推送给订阅了"/topic/user"的客户端
//            String destination = "/topic/user/";
//            messagingTemplate.convertAndSend(destination, message);
//            System.out.println("Sent message to user: " + message);
//
//            // 标记消息已处理，设置过期时间（比消息TTL长一些）
//            redisTemplate.opsForValue().set(redisKey, "processed", 60, TimeUnit.SECONDS);
//        }
//    }
//// ... existing code ...
