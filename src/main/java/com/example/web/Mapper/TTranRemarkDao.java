package com.example.web.Mapper;

import com.example.web.Bean.TTranRemark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TTranRemarkDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TTranRemark record);

    int insertSelective(TTranRemark record);

    TTranRemark selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TTranRemark record);

    int updateByPrimaryKey(TTranRemark record);


    List<TTranRemark> getByIdList(@Param("tranId") Integer tranId);
}