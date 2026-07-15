package com.example.web.service.impl;

import com.example.web.Bean.User;
import com.example.web.Mapper.WxUserDao;
import com.example.web.service.WxUserService;
import com.example.web.query.WxLogoutQuery;
import com.example.web.query.CancelSignUpData;
import com.example.web.query.SignUpActiveProductQuery;
import com.example.web.query.SignUpDateQuery;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WxUserServiceImpl implements WxUserService {
    @Resource
    private WxUserDao wxUser;

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
    public int signUpDate(SignUpDateQuery signUpDateQuery) {
        return wxUser.signUpDate(signUpDateQuery);
    }

    @Override
    public int cancelSigun(CancelSignUpData cancel) {
        return wxUser.cancelSigun(cancel);
    }

    @Override
    public SignUpActiveProductQuery getActJoinProductData(Integer actId) {
        return wxUser.signUpActiveProductQuery(actId);
    }

    @Override
    public int getRecentAddClueId() {
        return wxUser.getRecentAddClueId();
    }

    @Override
    public int byUserIDAndActiveIdDeleteClue(Integer actId, String userId) {
        return wxUser.byUserIDAndActiveIdDeleteClue(actId,userId);
    }
}
