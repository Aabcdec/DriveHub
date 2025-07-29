package com.example.web.Mapper;

import com.example.web.Bean.TPermission;

import java.util.List;

public interface TPermissionDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TPermission record);

    int insertSelective(TPermission record);

    TPermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TPermission record);

    int updateByPrimaryKey(TPermission record);

    List<TPermission> selectButtonPermissionByUserId(Integer id);
}