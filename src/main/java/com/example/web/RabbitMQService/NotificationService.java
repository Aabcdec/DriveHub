package com.example.web.RabbitMQService;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    // 清空指定用户队列
    public void clearUserQueue(Integer ownerId) {
        String queueName = "user.queue.direct." + ownerId;
        try {
            rabbitAdmin.purgeQueue(queueName);
            System.out.println("已清空队列: " + queueName);
        } catch (Exception e) {
            System.out.println("清空队列失败（可能队列不存在）: " + queueName);
        }
    }

    // 发送消息（自动创建队列和绑定）
    public void sendMessage(Integer ownerId, Object sendObject) {
//        // 先清空队列
//        clearUserQueue(ownerId);

        // 动态创建队列和绑定
        createUserQueueAndBinding(ownerId);

        // 发送消息
        String messageId = UUID.randomUUID().toString();
        String routingKey = "notification.message." + ownerId;

        CorrelationData correlationData = new CorrelationData(messageId);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                routingKey,
                sendObject,
                message -> {
                    message.getMessageProperties().setExpiration("86400000");
                    message.getMessageProperties().setMessageId(messageId);
                    message.getMessageProperties().setHeader("ownerId", ownerId);
                    return message;
                },
                correlationData
        );

        System.out.println("消息已发送到用户 " + ownerId + " 的队列");
    }

    // 动态创建队列和绑定
    private void createUserQueueAndBinding(Integer ownerId) {
        String queueName = "user.queue.direct." + ownerId;
        String routingKey = "notification.message." + ownerId;

        try {
            // 创建队列
            Queue queue = new Queue(queueName, true, false, false);
            rabbitAdmin.declareQueue(queue);

            // 创建绑定
            Binding binding = BindingBuilder.bind(queue)
                    .to(new DirectExchange("notification.exchange"))
                    .with(routingKey);
            rabbitAdmin.declareBinding(binding);

            System.out.println("已创建队列和绑定: " + queueName);

        } catch (Exception e) {
            System.out.println("创建队列和绑定失败: " + e.getMessage());
        }
    }

    // 单独的方法：只删除队列数据（不清空队列）
    public void deleteQueueData(Integer ownerId) {
        clearUserQueue(ownerId);
    }
}