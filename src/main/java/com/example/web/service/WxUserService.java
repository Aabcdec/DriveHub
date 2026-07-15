package com.example.web.service;

import com.example.web.Bean.User;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.CancelSignUpData;
import com.example.web.query.SignUpActiveProductQuery;
import com.example.web.query.SignUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;

public interface WxUserService {
    int savaUser(User user);
    int userLogout(WxLogoutQuery wxLogoutQuery);
    WxLogoutQuery checkUser(String phone);
    int signUpDate(SignUpDateQuery SignUpDateQuery);
    int cancelSigun(CancelSignUpData cancel);

    SignUpActiveProductQuery getActJoinProductData(Integer actId);

    int getRecentAddClueId();

    int byUserIDAndActiveIdDeleteClue(Integer actId, String userId);
}
