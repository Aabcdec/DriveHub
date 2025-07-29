package com.example.web.Servlet;

import com.example.web.Bean.TActivity;
import com.example.web.query.Images;
import com.example.web.query.myUpSignUpDataQuery;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ActivityServlet {
    List<TActivity> getActs(Integer pageNum,Integer pageSize);
    List<TActivity> getActAll();
    int updateByPrimaryKeySelective(TActivity tActivity);

    int addTActivity(TActivity tActivity);
    int deleteById(Integer id);
     List<TActivity> queryByIdAndDate(Integer id, Date startTime, Date endTime);
    List<TActivity>selectByIdsAct(List<Long> ids);
    TActivity selectByPrimaryKey(Integer id);
    int increaseParticipants(Integer id);
    Integer updateParty(Integer id);

    List<TActivity> selectByIdsSignUpData(myUpSignUpDataQuery myUpSignUpDataQuery);
}
