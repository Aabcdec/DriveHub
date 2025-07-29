package com.example.web.Controller;

import com.example.web.Bean.TRole;
import com.example.web.Servlet.TRoleServlet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TRoleController {
    @Resource
    private TRoleServlet tRoleServlet;
    @GetMapping("/byIdClue")
    public TRole selectByPrimaryKey(@RequestParam("id") Integer id){
        System.out.println(id);
        return tRoleServlet.selectByPrimaryKey(id);
    }
}
