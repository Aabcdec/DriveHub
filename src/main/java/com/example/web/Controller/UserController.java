package com.example.web.Controller;


import com.example.web.Bean.TActivity;
import com.example.web.Bean.TUser;
import com.example.web.Result.ApiResult;
import com.example.web.Servlet.UserServlet;
import com.example.web.query.IdListRequest;
import com.example.web.query.UserQuery;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@ApiResult
public class UserController {
    @Autowired
    private UserServlet userServlet;
    //分页
    @GetMapping("/users")
    public List<TUser> getUsers(@RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
    return userServlet.users(pageNum,pageSize);
    }
    @GetMapping("/getUsers")
    public List<TUser> getUsers(){
       return userServlet.getUsers();
    }
    @DeleteMapping("/user/{id}")
    public TUser byIdUser(@PathVariable("id") Integer id){
        return userServlet.byIdUser(id);
    }
    @GetMapping("/getUserById")
    public TUser getUserById(@RequestParam("id") Integer id){
        return userServlet.getUserById(id);
    }
    //登录页
    @PostMapping("/toLogin")
    public TUser getUserById(@RequestBody TUser tUser){
        return userServlet.getUser(tUser.getLoginAct(),tUser.getLoginPwd());
    }
    @GetMapping("/checkLogin")
    public String CheckLoginPwd(@RequestParam("loginAct") String loginAct){

        return userServlet.CheckLoginPwd(loginAct);
    }
    @PostMapping("/register")
    public Integer register(@RequestBody TUser tUser){
        System.out.println(tUser);
        return userServlet.register(tUser.getLoginAct(), tUser.getLoginPwd(),tUser.getName(),tUser.getPhone(),tUser.getEmail());
    }
    @GetMapping("/deleteById")
    public String getUsers(@RequestParam("id") Integer id){
        System.out.println(id);
        return userServlet.deleteById(id);
    }
    @PostMapping("/addUser")
    public Integer addUser(@RequestBody UserQuery userQuery, @RequestHeader("Authorization") String token){
        userQuery.setToken(token);
        return userServlet.save(userQuery);
    }
    @PostMapping("/updateUser")
    public Integer updateUser(@RequestBody UserQuery userQuery){
        return userServlet.updateUser(userQuery);
    }
    @PostMapping ("/batchDelete")
    public Integer batchDelete(@RequestBody IdListRequest ids){
        System.out.println(ids);
        Integer integer = userServlet.batchDelete(ids.getIds());
        System.out.println(integer);
        return integer;
    }

}
