package com.example.web.Mapper;

import com.example.web.Bean.TFollow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TFollowDao {
    int saveFollow(TFollow tFollow);
    List<TFollow> byIdFollow(@Param("fId")Integer fId);
    int byDeleteIdFollow(@Param("fId") Integer fId,@Param("id")Integer id);

    int tFollowDao(TFollow tFollow);

    int deteleByIdFollow(@Param("fId") Integer fId);
}
