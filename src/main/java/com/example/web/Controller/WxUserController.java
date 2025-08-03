package com.example.web.Controller;

import com.example.web.Bean.User;
import com.example.web.Servlet.WxUserServlet;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpDateQuery;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class WxUserController {
    @Resource
    private WxUserServlet wxUserServlet;
    @Resource
    private RedisTemplate redisTemplate;
    @PostMapping("/user/saveUserInfo")
    int savaUser(@RequestBody User user){
       return  wxUserServlet.savaUser(user);
    }
    @PostMapping("/user/logout")
    int userLogout(@RequestBody WxLogoutQuery wxLogoutQuery){
        return wxUserServlet.userLogout(wxLogoutQuery);
    }
    @GetMapping("/user/checkPhoneRegistration")
    WxLogoutQuery checkPhoneRegistration(@RequestParam("phone") String phone){
        System.out.println(phone);
        return wxUserServlet.checkUser(phone);
    }
    @PostMapping("/signUp")
    int signUpDate(@RequestBody signUpDateQuery signUpDateQuery){
        int i =wxUserServlet.signUpDate(signUpDateQuery);
        if(i>0){
            //情空缓存
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                connection.flushDb();
                return null;
            });
        }
        return i;

    }
    @PostMapping("/cancelSignUp")
    int cancelSigun(@RequestBody cancelSigunData cancel){
        System.out.println(cancel.toString());
       return  wxUserServlet.cancelSigun(cancel);
    }
}
