package com.example.web.service;

import com.example.web.Bean.TFollow;

import java.util.List;

public interface FollowService {
    int saveFollow(TFollow tFollow);
    List<TFollow> byIdFollow(Integer fId);
    int byDeleteIdFollow(Integer fId,Integer id);

    int updateFollow(TFollow tFollow);

    int deteleByIdFollow(Integer fId);
}
