package com.example.web.RabbitMQService;
import com.example.web.Config.RabbitMQConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class NotificationService {

    @Resource
    private RabbitMQConfig rabbitMQConfig;


    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Integer ownerId,Object sendObject){
        String messageId = UUID.randomUUID().toString();

        // 创建消息相关数据
        CorrelationData correlationData = new CorrelationData(messageId);
        //更新rabbitMQ中的路由key
        //管理员id为1 路由key则应该为notification.message.1
        rabbitMQConfig.setRouterKey("notification.message."+ownerId);
        //拿到 最新的路由key
        String routerKey=rabbitMQConfig.getRouterKey();
        // 发送对象，并设置消息属性
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.Direct_EXCHANGE_NAME,
                routerKey,
                sendObject,
                message -> {
                    //消息默认一天过期 但是只要用户点击了我的待办则会消费 这里应该在用户首页加载的时候往mq中添加消息
                    //在用户点击我的待办的时候通过websocket连接mq 拿到所有数据同步到vuex 也同步到本地
                    //防止数据丢失 易于数据恢复 这里并没有在mq中做防重 前端通过websocket每次拿到数据可以自行进行去重复
                    message.getMessageProperties().setExpiration("86400000");
                    message.getMessageProperties().setMessageId(messageId);
                    return message;
                },
                correlationData
        );
    }

}