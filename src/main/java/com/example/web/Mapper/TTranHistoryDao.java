package com.example.web.Mapper;

import com.example.web.Bean.TTranHistory;

public interface TTranHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TTranHistory record);

    int insertSelective(TTranHistory record);

    TTranHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TTranHistory record);

    int updateByPrimaryKey(TTranHistory record);
}