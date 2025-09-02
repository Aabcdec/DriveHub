package com.example.web.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DIRECT_EXCHANGE_NAME = "notification.exchange";

    // 创建直连交换机
    @Bean
    public DirectExchange notificationExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    // 配置消息转换方式
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 创建RabbitAdmin
    @Bean
    public RabbitAdmin rabbitAdmin(org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        return new RabbitAdmin(rabbitTemplate);
    }
}