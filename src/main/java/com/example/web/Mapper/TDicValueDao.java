package com.example.web.Mapper;

import com.example.web.Bean.TDicValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TDicValueDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TDicValue record);

    int insertSelective(TDicValue record);

    List<TDicValue> selectByPrimaryKey(@Param("pageNum") Integer pageNum,
                                 @Param("pageSize") Integer pageSize,
                                 @Param("typeCode") String typeCode);

    int updateByPrimaryKeySelective(TDicValue record);

    int updateByPrimaryKey(TDicValue record);

}