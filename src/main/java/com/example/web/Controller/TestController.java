package com.example.web.Controller;

import com.example.web.RabbitMQService.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private NotificationService notificationService;

//    @GetMapping("/send-test")
//    public String sendTestMessage() {
//        notificationService.sendMessage();
//        return "测试消息已发送";
//    }
}