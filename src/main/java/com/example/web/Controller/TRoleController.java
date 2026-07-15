package com.example.web.Controller;

import com.example.web.Bean.TRole;
import com.example.web.Bean.TUser;
import com.example.web.service.RoleService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色与用户-角色分配接口。
 * <p>同时保留历史路径 {@code /getroles} 与规范路径 {@code /roles*}，以兼容已对接前端。
 */
@Slf4j
@RestController
public class TRoleController {

    @Resource
    private RoleService roleService;

    /** 兼容历史：按 id 查角色（路径名 byIdClue 不可变） */
    @GetMapping("/byIdClue")
    public TRole selectByPrimaryKey(@RequestParam("id") Integer id) {
        return roleService.selectByPrimaryKey(id);
    }

    @GetMapping("/roles/{id}")
    public List<TUser> roleByUser(@PathVariable("id") Integer id) {
        return roleService.getRoleByUser(id);
    }

    @Data
    public static class AssignRequest {
        private Integer roleId;
        private List<Long> userIds;
    }

    @PostMapping("/roles")
    public int displayRole(@RequestBody AssignRequest assignRequest) {
        return roleService.addRole(assignRequest.getRoleId(), assignRequest.getUserIds());
    }

    @Data
    public static class addRoleBean {
        private String role;
        private String roleName;
    }

    @PostMapping("/addRoles")
    public int addRoles(@RequestBody addRoleBean addRoleBean) {
        return roleService.addRoles(addRoleBean);
    }

    @GetMapping("/roles")
    public List<TRole> getRoles() {
        return roleService.getRoles();
    }

    /** 兼容历史列表接口（原 RoleController） */
    @GetMapping("/getroles")
    public List<TRole> getRolesLegacy() {
        return roleService.getRole();
    }

    @PutMapping("/roles/{id}")
    public int updateRole(@PathVariable("id") Integer id, @RequestBody addRoleBean addRoleBean) {
        return roleService.updataRole(id, addRoleBean);
    }

    @DeleteMapping("/roles/{id}")
    public int deleteRole(@PathVariable("id") Integer id) {
        return roleService.deleteRole(id);
    }
}
