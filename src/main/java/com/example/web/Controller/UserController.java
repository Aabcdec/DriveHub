package com.example.web.Controller;


import com.example.web.Bean.TActivity;
import com.example.web.Bean.TUser;
import com.example.web.Result.ApiResult;
import com.example.web.service.UserService;
import com.example.web.query.IdListRequest;
import com.example.web.query.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台用户 REST 接口。类级 {@link com.example.web.Result.ApiResult} 会将返回值包装为 ApiResponse。
 */
@RestController
@ApiResult
public class UserController {
    @Autowired
    private UserService userService;
    //分页
    @GetMapping("/users")
    public List<TUser> getUsers(@RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
    return userService.users(pageNum,pageSize);
    }
    @GetMapping("/getUsers")
    public List<TUser> getUsers(){
       return userService.getUsers();
    }
    @DeleteMapping("/user/{id}")
    public TUser byIdUser(@PathVariable("id") Integer id){
        return userService.byIdUser(id);
    }
    @GetMapping("/getUserById")
    public TUser getUserById(@RequestParam("id") Integer id){
        return userService.getUserById(id);
    }
    //登录页
    @PostMapping("/toLogin")
    public TUser getUserById(@RequestBody TUser tUser){
        return userService.getUser(tUser.getLoginAct(),tUser.getLoginPwd());
    }
    @GetMapping("/checkLogin")
    public String CheckLoginPwd(@RequestParam("loginAct") String loginAct){

        return userService.CheckLoginPwd(loginAct);
    }
    @PostMapping("/register")
    public Integer register(@RequestBody TUser tUser){
        System.out.println(tUser);
        return userService.register(tUser.getLoginAct(), tUser.getLoginPwd(),tUser.getName(),tUser.getPhone(),tUser.getEmail());
    }
    @GetMapping("/deleteById")
    public String getUsers(@RequestParam("id") Integer id){
        System.out.println(id);
        return userService.deleteById(id);
    }
    @PostMapping("/addUser")
    public Integer addUser(@RequestBody UserQuery userQuery, @RequestHeader("Authorization") String token){
        userQuery.setToken(token);
        return userService.save(userQuery);
    }
    @PostMapping("/updateUser")
    public Integer updateUser(@RequestBody UserQuery userQuery){
        return userService.updateUser(userQuery);
    }
    @PostMapping ("/batchDelete")
    public Integer batchDelete(@RequestBody IdListRequest ids){
        System.out.println(ids);
        Integer integer = userService.batchDelete(ids.getIds());
        System.out.println(integer);
        return integer;
    }

}
