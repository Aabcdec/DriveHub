package com.example.web.Servlet;

import com.example.web.Bean.User;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpDateQuery;

public interface WxUserServlet {
    int savaUser(User user);
    int userLogout(WxLogoutQuery wxLogoutQuery);
    WxLogoutQuery checkUser(String phone);
    int signUpDate(signUpDateQuery signUpDateQuery);
    int cancelSigun(cancelSigunData cancel);
}
