package com.example.web.Mapper;



import com.example.web.Bean.TUser;
import com.example.web.commons.DataScope;
import com.example.web.query.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Mapper
@Repository
public interface UserMapper {
//@Select("select * from t_user")
    List<TUser> getUsers();
//@Select("select * from t_user where login_act=#{loginAct} and login_pwd=#{loginPwd}")
    //登录

    TUser getUser(@Param("loginAct") String loginAct, @Param("loginPwd") String loginPwd);
    String CheckLoginPwd(@Param("loginAct")String loginAct);
    Integer register(@Param("loginAct") String loginAct,
                     @Param("loginPwd") String loginPwd,
                     @Param("name") String name,
                     @Param("phone") String phone,
                     @Param("email") String email
    );
    String deleteById(@Param("id")Integer id);

    Integer updateUser(TUser tUser);
    //分页 起始页码=(当前页码-1)*每页展示多少数据
//    @Select("select * from t_user LIMIT #{pageNum},#{pageSize}")

    List<TUser> users(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);
   @Select("select * from t_user where id=#{id}")
    TUser byIdUser(@Param("id")Integer id);
    Integer insertSelective(TUser tUser);
    //批量删除
    Integer batchDelete(@Param("ids") List<Long> ids);
    TUser getUserById(@Param("id")Integer id);


}
