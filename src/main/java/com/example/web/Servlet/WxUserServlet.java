package com.example.web.Servlet;

import com.example.web.Bean.User;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpActiveProductQuery;
import com.example.web.query.signUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;

public interface WxUserServlet {
    int savaUser(User user);
    int userLogout(WxLogoutQuery wxLogoutQuery);
    WxLogoutQuery checkUser(String phone);
    int signUpDate(signUpDateQuery signUpDateQuery);
    int cancelSigun(cancelSigunData cancel);

    signUpActiveProductQuery getActJoinProductData(Integer actId);

    int getRecentAddClueId();

    int byUserIDAndActiveIdDeleteClue(Integer actId, String userId);
}
