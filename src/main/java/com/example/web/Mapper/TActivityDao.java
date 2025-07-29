package com.example.web.Mapper;

import com.example.web.Bean.TActivity;
import com.example.web.commons.DataScope;
import com.example.web.query.BaseQuery;
import com.example.web.query.myUpSignUpDataQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TActivityDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TActivity record);

    int insertSelective(TActivity record);

    TActivity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TActivity record);

    int updateByPrimaryKey(TActivity record);
    @DataScope(tableAlias="a",tableField = "owner_id")
    List<TActivity> getActs(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("query") BaseQuery baseQuery);
    List<TActivity> getActAll();
    List<TActivity> selectByIdAndDateRange(TActivity tActivity);
    List<TActivity> selectByIdsAct(@Param("ids") List<Long> ids);
    List<TActivity> selectByIdsSignUpData(myUpSignUpDataQuery myUpSignUpDataQuery);
    @Update("UPDATE t_activity SET party = party + 1 WHERE id = #{id}")
    int increaseParticipants(@Param("id") Integer id);
    //从小程序来的数据通知人数加一
    Integer updateParty(@Param("id") Integer id);

    Integer selecOngoingActivity();

    Integer selectByCount();
}