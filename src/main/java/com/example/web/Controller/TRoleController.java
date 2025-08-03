package com.example.web.Controller;

import com.example.web.Bean.TRole;
import com.example.web.Bean.TUser;
import com.example.web.Servlet.TRoleServlet;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TRoleController {
    @Resource
    private TRoleServlet tRoleServlet;
    @GetMapping("/byIdClue")
    public TRole selectByPrimaryKey(@RequestParam("id") Integer id){
        System.out.println(id);
        return tRoleServlet.selectByPrimaryKey(id);
    }
    @GetMapping("/roles/{id}")
    public List<TUser> roleByUser(@PathVariable("id") Integer id){
        return tRoleServlet.getRoleByUser(id);
    }
    @Data // Lombok注解
    public static class AssignRequest {
        private Integer roleId;
        private List<Long> userIds;
    }

    @PostMapping("/roles")
    public int displayRole(@RequestBody AssignRequest assignRequest){
        return tRoleServlet.addRole(assignRequest.getRoleId(),assignRequest.getUserIds());
    }
    @Data
    public static class addRoleBean{
        private String role;
        private String roleName;
    }
    @PostMapping("/addRoles")
    public int addRoles(@RequestBody addRoleBean addRoleBean){
        return tRoleServlet.addRoles(addRoleBean);
    }
    @GetMapping("/roles")
    public List<TRole> getRoles(){
        return tRoleServlet.getRoles();
    }
    @PutMapping("/roles/{id}")
    public int updateRole(@PathVariable("id")Integer id,@RequestBody addRoleBean addRoleBean ){
        return tRoleServlet.updataRole(id,addRoleBean);
    }
    @DeleteMapping("/roles/{id}")
    public int deleteRole(@PathVariable("id")Integer id){
        return tRoleServlet.deleteRole(id);
    }
}
