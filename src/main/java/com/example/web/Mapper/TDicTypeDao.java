package com.example.web.Mapper;

import com.example.web.Bean.TDicType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TDicTypeDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TDicType record);

    int insertSelective(TDicType record);

    TDicType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TDicType record);

    int updateByPrimaryKey(TDicType record);

    List<TDicType> getAll();
    List<TDicType> getTypeAll(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);


}