package com.example.web.Controller;

import com.example.web.RabbitMQService.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class WebSocketController {

    @Autowired
    private NotificationService notificationService;

//    // 处理客户端发送的消息（如果需要双向通信）
//    @MessageMapping("/send.message")
//    public void handleChatMessage(String message, Principal principal) {
//        String userId = principal.getName();
//        System.out.println("Received message from user " + userId + ": " + message);
//        // 这里可以处理业务逻辑，例如调用notificationService转发消息
//    }
//
//    // 当客户端订阅个人主题时，为其创建RabbitMQ队列
//    // 注意：这不是一个标准的STOMP映射，通常需要在连接后由客户端显式调用API触发
//    // 更常见的做法是在用户登录成功后，调用notificationService.setupUserQueue(userId)
//    @SubscribeMapping("/topic/user/**")
//    public void onUserSubscribe(Principal principal) {
//        if (principal != null) {
//            String userId = principal.getName();
//            notificationService.sendMessage(Integer ownerId,Object sendObject);
//        }
//    }
}
