package com.example.web.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 启用STOMP基于代理的消息传递
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个STOMP端点，客户端将使用它连接到我们的服务器
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 允许所有源（生产环境应指定具体前端地址）
                .withSockJS(); // 启用SockJS回退选项
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用一个简单的内存消息代理，目的地以“/topic”开头
        registry.enableSimpleBroker("/topic", "/queue");
        // 配置客户端发送消息的请求前缀为“/app”
        registry.setApplicationDestinationPrefixes("/app");
    }
}
