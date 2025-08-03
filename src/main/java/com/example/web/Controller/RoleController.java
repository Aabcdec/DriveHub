package com.example.web.Controller;

import com.example.web.Bean.TRole;
import com.example.web.Servlet.RoleServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class RoleController {
    @Resource
    private RoleServer roleServer;
    @GetMapping("/getroles")
    public List<TRole> getRoles(){
        return roleServer.getRole();
    }
}
