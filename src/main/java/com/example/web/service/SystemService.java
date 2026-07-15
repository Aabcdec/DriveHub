package com.example.web.service;

import com.example.web.Bean.TSystemInfo;

import java.util.List;

public interface SystemService {
    List<TSystemInfo> getSystemInfos();

    int updateSystemInfo(TSystemInfo tSystemInfo);
}
