package com.example.web.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.stream.events.StartElement;

@Configuration
public class RabbitMQConfig {

    public static final String Direct_EXCHANGE_NAME = "notification.exchange";
    public static final String QUEUE_PREFIX = "user.queue.direct.a";
    private String routerKey="notification.message.1";

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }

    public String getRouterKey() {
        return routerKey;
    }

    //创建直连交换机
    @Bean
    public DirectExchange NormalExchange(){
        return  ExchangeBuilder.directExchange(Direct_EXCHANGE_NAME).build();
    }
    //创建主队交互队列
    @Bean
    public Queue queueA(){
        return QueueBuilder.durable(QUEUE_PREFIX).build();
    }
    //交换机和队列进行绑定
    @Bean
    public Binding bindingA(DirectExchange directExchange, Queue queueA){
        // 队列   直连交换机  路由key
        return BindingBuilder.bind(queueA).to(directExchange).with(routerKey);
    }
    //配置消息转换方式，使用json序列化方式
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}