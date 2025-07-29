package com.example.web.Controller;

import com.example.web.Bean.User;
import com.example.web.Servlet.WxUserServlet;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpDateQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class WxUserController {
    @Resource
    private WxUserServlet wxUserServlet;
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
        return wxUserServlet.signUpDate(signUpDateQuery);
    }
    @PostMapping("/cancelSignUp")
    int cancelSigun(@RequestBody cancelSigunData cancel){
        System.out.println(cancel.toString());
       return  wxUserServlet.cancelSigun(cancel);
    }
}
