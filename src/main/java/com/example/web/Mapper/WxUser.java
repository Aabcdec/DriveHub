package com.example.web.Mapper;

import com.example.web.Bean.User;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpDateQuery;
import org.apache.ibatis.annotations.Param;

public interface WxUser {
    int savaUser(User user);
    int userLogout(WxLogoutQuery wxLogoutQuery);
    WxLogoutQuery checkUser(@Param("phone") String phone);//用手机号验证 手机号唯一
    int signUpDate(signUpDateQuery signUpDateQuery);

    int cancelSigun(cancelSigunData cancel);


}
