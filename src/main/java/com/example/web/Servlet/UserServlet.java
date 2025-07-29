package com.example.web.Servlet;



import com.example.web.Bean.TUser;
import com.example.web.query.UserQuery;
import org.apache.ibatis.annotations.Param;


import java.util.ArrayList;
import java.util.List;

public interface UserServlet {
//    Page<TUser> getUsers(int page, int size);

    // 分页查询用户
//
//    public Page<TUser> getUsers(int page, int size) {
//        // 创建 Pageable 对象，指定页码、每页大小和排序方式
//        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
//        return userRepository.findAll(pageable);
//    }
    List<TUser> getUsers();
//分页
    List<TUser> users(Integer pageNum,Integer pageSize);
    //登录
    TUser getUser(String loginAct, String loginPwd);
    String CheckLoginPwd(String loginAct);
    Integer register(@Param("loginAct") String loginAct,
                     @Param("loginPwd")String loginPwd,
                     @Param("name")String name,
                     @Param("phone")String phone,
                     @Param("email")String email
    );
    String deleteById(@Param("id")Integer id);
//根据id查看数据
    TUser byIdUser(Integer id);

    Integer save(UserQuery userQuery);
    Integer batchDelete(List<Long> ids);
    TUser getUserById(Integer id);
    Integer updateUser(UserQuery userQuery);
}
