package com.example.web.Servlet;

import com.example.web.Bean.TSystemInfo;

import java.util.List;

public interface SystemServer {
    List<TSystemInfo> getSystemInfos();

    int updateSystemInfo(TSystemInfo tSystemInfo);
}
