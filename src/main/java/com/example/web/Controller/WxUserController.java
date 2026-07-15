package com.example.web.Controller;

import com.example.web.Bean.User;
import com.example.web.service.WxUserService;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.CancelSignUpData;
import com.example.web.query.SignUpActiveProductQuery;
import com.example.web.query.SignUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class WxUserController {
    @Resource
    private WxUserService wxUserService;
    @Resource
    private RedisTemplate redisTemplate;
    @PostMapping("/user/saveUserInfo")
    int savaUser(@RequestBody User user){
       return  wxUserService.savaUser(user);
    }
    @PostMapping("/user/logout")
    int userLogout(@RequestBody WxLogoutQuery wxLogoutQuery){
        return wxUserService.userLogout(wxLogoutQuery);
    }
    @GetMapping("/user/checkPhoneRegistration")
    WxLogoutQuery checkPhoneRegistration(@RequestParam("phone") String phone){
        System.out.println(phone);
        return wxUserService.checkUser(phone);
    }
    @GetMapping("/getActJoinProductData")
    SignUpActiveProductQuery getActJoinProductData(@RequestParam("actId")Integer actId){
        return wxUserService.getActJoinProductData(actId);
    }
    @PostMapping("/signUp")
    int signUpDate(@RequestBody SignUpDateQuery SignUpDateQuery){
        int i =wxUserService.signUpDate(SignUpDateQuery);
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
        return wxUserService.getRecentAddClueId();
    }
    //取消报名
    @PostMapping("/cancelSignUp")
    int cancelSigun(@RequestBody CancelSignUpData cancel){
        System.out.println(cancel.toString());
        int result=wxUserService.cancelSigun(cancel);
        if(result>0){
            int resultInner=wxUserService.byUserIDAndActiveIdDeleteClue(cancel.getActivityId(),cancel.getUserId());
            System.out.println("小程序取消报名线索软删除情况"+resultInner);
            return result;
        }else{
            return  0;
        }

    }

}
