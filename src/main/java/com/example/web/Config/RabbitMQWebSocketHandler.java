package com.example.web.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// RabbitMQWebSocketHandler.java
@Component
public class RabbitMQWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String queueName = message.getPayload().trim();

        if (queueName.isEmpty()) {
            session.sendMessage(new TextMessage("错误: 队列名不能为空"));
            return;
        }

        try {
            // 获取队列消息数量
            AMQP.Queue.DeclareOk declareOk = rabbitTemplate.execute(channel ->
                    channel.queueDeclarePassive(queueName));
            int messageCount = declareOk.getMessageCount();

            // 发送开始消息
            session.sendMessage(new TextMessage(
                    "开始获取队列: " + queueName + ", 消息数量: " + messageCount
            ));

            // 消费所有消息并发送给前端
            for (int i = 0; i < messageCount; i++) {
                Object messageObj = rabbitTemplate.receiveAndConvert(queueName);
                if (messageObj != null) {
                    String jsonMessage = objectToJson(messageObj);
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            }

            // 发送完成消息
            session.sendMessage(new TextMessage(
                    "完成: 已获取队列 " + queueName + " 的所有 " + messageCount + " 条消息"
            ));

        } catch (Exception e) {
            session.sendMessage(new TextMessage("错误: " + e.getMessage()));
        }
    }

    private String objectToJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\": \"JSON转换失败: " + e.getMessage() + "\"}";
        }
    }
}
