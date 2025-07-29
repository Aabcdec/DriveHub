package com.example.web.Mapper;

import com.example.web.Bean.TTran;
import com.example.web.query.StatisticsQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TTranDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TTran record);

    int insertSelective(TTran record);

    TTran selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TTran record);

    int updateByPrimaryKey(TTran record);

    BigDecimal selectBySuccessTranAmount();

    BigDecimal selectByTotalTranAmount();

    int selectByTotalTranCount();

    int selectBySuccessTranCount();

    List<TTran> getTran(@Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize);

    StatisticsQuery getStatistics();


    List<TTran> searchTran(TTran tTran);
}