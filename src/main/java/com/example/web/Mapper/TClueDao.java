package com.example.web.Mapper;

import com.example.web.Bean.TActivity;
import com.example.web.Bean.TClue;
import com.example.web.Bean.TCustomer;
import com.example.web.Result.NameValue;
import com.example.web.commons.DataScope;
import com.example.web.query.BaseQuery;
import com.example.web.query.MonthLineData;
import com.example.web.query.YearLineData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TClueDao {

    void saveClue(@Param("tClueList") List<TClue> tClueList);

    int deleteByPrimaryKey(Integer id);

    int insert(TClue record);

    int insertSelective(TClue record);

    TClue selectByPrimaryKey(@Param("id")Integer id);

    int updateByPrimaryKeySelective(TClue record);

    int updateByPrimaryKey(TClue record);
    @DataScope(tableAlias="tc",tableField = "owner_id")
    List<TClue> getClues(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("query") BaseQuery baseQuery);
    List<TClue> selectByIdAndDateRange(TClue tClue);

    int deteleByIdClue(Integer id);

    List<TCustomer> selectCustomerPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    Integer selectClueByCount();

    List<NameValue> selectBySource();

    List<YearLineData> getByYearLineDate();

    List<MonthLineData> getByMouthLineDate();

    List<YearLineData> getByYearLineXConverterDate();

    List<MonthLineData> getByMouthLineConveterData();
}