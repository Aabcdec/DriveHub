package com.example.web.Servlet;

import com.example.web.Result.NameValue;
import com.example.web.Result.R;
import com.example.web.Result.SummaryData;

import java.util.List;

public interface StatisticService {
    SummaryData loadSummaryData();

    List<NameValue> loadSaleFunnelData();

    List<NameValue> loadSourcePieData();

    List<R> loadLineData(String range);

    List<R> lineConverterData(String range);
}
