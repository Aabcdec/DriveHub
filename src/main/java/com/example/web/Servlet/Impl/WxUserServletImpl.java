package com.example.web.Servlet.Impl;

import com.example.web.Bean.User;
import com.example.web.Mapper.WxUser;
import com.example.web.Servlet.WxUserServlet;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.cancelSigunData;
import com.example.web.query.signUpDateQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WxUserServletImpl implements WxUserServlet {
    @Resource
    private WxUser wxUser;

    @Override
    public int savaUser(User user) {
        return wxUser.savaUser(user);
    }

    @Override
    public int userLogout(WxLogoutQuery wxLogoutQuery) {
        return wxUser.userLogout(wxLogoutQuery);
    }

    @Override
    public WxLogoutQuery checkUser(String phone) {
        return wxUser.checkUser(phone);
    }

    @Override
    public int signUpDate(signUpDateQuery signUpDateQuery) {
        return wxUser.signUpDate(signUpDateQuery);
    }

    @Override
    public int cancelSigun(cancelSigunData cancel) {
        return wxUser.cancelSigun(cancel);
    }
}
