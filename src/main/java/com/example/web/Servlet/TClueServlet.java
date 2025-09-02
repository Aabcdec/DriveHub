package com.example.web.Servlet;

import com.example.web.Bean.TClue;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;
import java.util.List;

public interface TClueServlet {
    TClue selectByPrimaryKey(@Param("id")Integer id);
    public List<TClue> getClues(Integer pageNum, Integer pageSize);
    int insertSelective(TClue tClue);
    int updateByPrimaryKeySelective(TClue tClue);
    List<TClue> selectByIdAndDateRange(TClue tClue);

    void importExcel(InputStream inputStream,int createId);

    int deteleByIdClue(@Param("id") Integer id);

    int updataHeader(@Param("fid") Integer fid);

    List<TClue> overdueClueList(@Param("fiownerIdd") Integer ownerId);
}
