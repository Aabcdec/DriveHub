package com.example.web.Controller;

import com.example.web.Bean.User;
import com.example.web.Servlet.WxUserServlet;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpActiveProductQuery;
import com.example.web.query.signUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
    @GetMapping("/getActJoinProductData")
    signUpActiveProductQuery getActJoinProductData(@RequestParam("actId")Integer actId){
        return wxUserServlet.getActJoinProductData(actId);
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
    //拿到当前报名后插入的线索ID
    @GetMapping("/getRecentAddClueId")
    int getRecentAddClueId(){
        return wxUserServlet.getRecentAddClueId();
    }
    //取消报名
    @PostMapping("/cancelSignUp")
    int cancelSigun(@RequestBody cancelSigunData cancel){
        System.out.println(cancel.toString());
        int result=wxUserServlet.cancelSigun(cancel);
        if(result>0){
            int resultInner=wxUserServlet.byUserIDAndActiveIdDeleteClue(cancel.getActivityId(),cancel.getUserId());
            System.out.println("小程序取消报名线索软删除情况"+resultInner);
            return result;
        }else{
            return  0;
        }

    }

}
