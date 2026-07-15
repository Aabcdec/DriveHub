package com.example.web.Mapper;

import com.example.web.Bean.User;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.CancelSignUpData;
import com.example.web.query.SignUpActiveProductQuery;
import com.example.web.query.SignUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.ibatis.annotations.Param;

public interface WxUserDao {
    int savaUser(User user);
    int userLogout(WxLogoutQuery wxLogoutQuery);
    WxLogoutQuery checkUser(@Param("phone") String phone);//用手机号验证 手机号唯一
    int signUpDate(SignUpDateQuery signUpDateQuery);

    int cancelSigun(CancelSignUpData cancel);

    SignUpActiveProductQuery signUpActiveProductQuery(@Param("actId") Integer actId);

    int getRecentAddClueId();

    int byUserIDAndActiveIdDeleteClue(@Param("actId") Integer actId, @Param("userId") String userId);
}
