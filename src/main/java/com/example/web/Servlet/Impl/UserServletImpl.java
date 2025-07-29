package com.example.web.Servlet.Impl;


import com.example.web.Bean.TPermission;
import com.example.web.Bean.TUser;
import com.example.web.Encode.SimplePasswordEncoder;
import com.example.web.Mapper.TPermissionDao;
import com.example.web.Mapper.UserMapper;
import com.example.web.Servlet.UserServlet;
import com.example.web.query.TokenWrapper;
import com.example.web.query.UserQuery;
import com.example.web.query.tokenBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.web.Encode.SimplePasswordEncoder.encode;

@Service
public class UserServletImpl implements UserServlet {
   private final static String salt = "uEklWxHWMepj32U4A5UGkQ==";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SimplePasswordEncoder simplePasswordEncoder;
    @Autowired
    private TPermissionDao tPermissionDao;
    @Override
    public List<TUser> getUsers() {
        return userMapper.getUsers();
    }
    //分页
    @Override
    public List<TUser> users(Integer pageNum, Integer pageSize) {
        //起始页等于页码减一*页面大小
        pageNum=(pageNum-1)*pageSize;
        return userMapper.users(pageNum,pageSize);
    }
//登录
    @Override
    public TUser getUser( String loginAct,  String loginPwd) {
        String encoded = encode(loginPwd, salt);


        String inputEncode=encode(loginPwd,salt);
        if(inputEncode.equals(encoded)){
            TUser user = userMapper.getUser(loginAct, inputEncode);
            //查询一下该用户有哪些功能权限
            List<TPermission> buttonPermissionList = tPermissionDao.selectButtonPermissionByUserId(user.getId());
            List<String> stringPermissionList = new ArrayList<>();
            buttonPermissionList.forEach(tPermission -> {
                stringPermissionList.add(tPermission.getCode());//权限标识符
            });
            user.setPermissionList(stringPermissionList);//设置用户的权限标识符
            return user;
        }

        return null;
    }

    @Override
    public String CheckLoginPwd(String loginAct) {
        System.out.println("servlet"+loginAct);
        //asL1PJIpEWeWfRSdi9a57KzRr5zhZb0ftaM4Bd4sWp8=
        String loginPwd=userMapper.CheckLoginPwd(loginAct);

        return loginPwd;
    }

    @Override
    public Integer register(String loginAct, String loginPwd, String name, String phone, String email) {
        String encoded = encode(loginPwd, salt);
        return userMapper.register(loginAct,encoded,name,phone,email);
    }
//
//    @Override
//    public Integer register(String loginAct, String loginPwd) {
//        String encoded = encode(loginPwd, salt);
//        return userMapper.register(loginAct,encoded);
//    }

    @Override
    public String deleteById(Integer id) {
        System.out.println(id);
        return userMapper.deleteById(id);
    }

    @Override
    public TUser byIdUser(Integer id) {
        return userMapper.byIdUser(id);
    }

    @Override
    public Integer save(UserQuery userQuery) {
        String loginPwd = userQuery.getLoginPwd();
        String encoded = encode(loginPwd, salt);
        userQuery.setLoginPwd(encoded);
        String token = userQuery.getToken();
        ObjectMapper mapper = new ObjectMapper();

        //把userQuery的数据赋值给TUser
        TUser tUser=new TUser();
        //下面代码把userQuery赋值给tUser要求属性值属性类型相同
        BeanUtils.copyProperties(userQuery,tUser);
        tUser.setCreateTime(new Date());
        try {
            // 核心方法：将JSON字符串转为对象
            tokenBean user = mapper.readValue(token, TokenWrapper.class).getValue();
            System.out.println("id: " + user.getId()); // 输出: Alice
            tUser.setCreateBy(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userMapper.insertSelective(tUser);
    }
//批量删除
    @Override
    public Integer batchDelete(List<Long> ids) {

        return userMapper.batchDelete(ids);
    }

    @Override
    public TUser getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Override
    public Integer updateUser(UserQuery userQuery) {
        //拿到当前字段 和传过来的字段做比较
        Integer id = userQuery.getId();
        TUser user = userMapper.getUserById(id);
        //编辑用户不可改
        userQuery.setLoginAct(null);
        if(userQuery.getEmail().equals(user.getEmail())){
            userQuery.setEmail(null);
        }
        if (userQuery.getPhone().equals(user.getPhone())){
            userQuery.setPhone(null);
        }

        if(userQuery.getLoginPwd()!=null){
            if(userQuery.getLoginPwd().equals(user.getLoginPwd())){
                userQuery.setLoginPwd(null);
            }else{
                String loginPwd = userQuery.getLoginPwd();
                String encoded = encode(loginPwd, salt);
                userQuery.setLoginPwd(encoded);
            }

        }


        //把userQuery的数据赋值给TUser
        TUser tUser=new TUser();
        //下面代码把userQuery赋值给tUser要求属性值属性类型相同
        BeanUtils.copyProperties(userQuery,tUser);
        tUser.setEditTime(new Date());

        return userMapper.updateUser(tUser);
    }
}
