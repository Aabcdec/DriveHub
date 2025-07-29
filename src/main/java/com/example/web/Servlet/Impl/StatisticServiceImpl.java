package com.example.web.Servlet.Impl;

import com.example.web.Result.NameValue;
import com.example.web.Result.R;
import com.example.web.Result.SummaryData;
import com.example.web.Servlet.StatisticService;
import com.example.web.manager.StatisticManager;
import com.example.web.query.MonthLineData;
import com.example.web.query.YearLineData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class StatisticServiceImpl implements StatisticService {
    @Resource
    private StatisticManager statisticManager;
    @Override
    public SummaryData loadSummaryData() {
        return statisticManager.loadSummaryData();
    }

    @Override
    public List<NameValue> loadSaleFunnelData() {
        return statisticManager.loadSaleFunnelData();
    }

    @Override
    public List<NameValue> loadSourcePieData() {
        return statisticManager.loadSourcePieData();
    }

    @Override
    public List<R> loadLineData(String range) {
        if(range.equals("year")){
            //按年统计
            List<YearLineData> byYearLineDate = statisticManager.getByYearLineDate();
            return (List<R>) R.OK(byYearLineDate).getData();
        }else if(range.equals("month")){
            //按月统计
            List<MonthLineData> byMouthLineDate = statisticManager.getByMouthLineDate();
            return (List<R>) R.OK(byMouthLineDate).getData();
        }else{
            //按周统计
        }
        return null;
    }

    @Override
    public List<R> lineConverterData(String range) {
        if (range.equals("year")) {
            //按年统计
            List<YearLineData> byYearLineDate = statisticManager.getByYearLineXConverterDate();
            return (List<R>) R.OK(byYearLineDate).getData();
        } else if (range.equals("month")) {
            //按月统计
            List<MonthLineData> byMouthLineConveterData = statisticManager.getByMouthLineConveterData();
            return (List<R>) R.OK(byMouthLineConveterData).getData();
        } else{

        }
        return null;
    }
}
