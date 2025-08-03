package com.example.web.Mapper;

import com.example.web.Bean.TSystemInfo;

import java.util.List;

public interface TSystemInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TSystemInfo record);

    int insertSelective(TSystemInfo record);

    TSystemInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TSystemInfo record);

    int updateByPrimaryKey(TSystemInfo record);

    List<TSystemInfo> getSystemInfos();
}