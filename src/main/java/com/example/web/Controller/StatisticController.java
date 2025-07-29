package com.example.web.Controller;


import com.example.web.Result.NameValue;
import com.example.web.Result.R;
import com.example.web.Result.SummaryData;
import com.example.web.Servlet.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据统计Controller
 */
@RestController
public class StatisticController {

    @Resource
    private StatisticService statisticService;

    @GetMapping(value = "/summary/data")
    public R summaryData() {
        SummaryData summaryData = statisticService.loadSummaryData();
        return R.OK(summaryData);
    }
    @GetMapping(value = "/lineData")
    public List<R> lineData(@RequestParam("range") String  range) {
        return statisticService.loadLineData(range);
    }
    @GetMapping(value = "/lineConverterData")
    public List<R> lineConverterData(@RequestParam("range") String  range) {
        return statisticService.lineConverterData(range);
    }
    @GetMapping(value = "/saleFunnel/data")
    public R saleFunnelData() {
        /**
         * [
         *    { value: 20, name: '成交' },
         *    { value: 60, name: '交易' },
         *    { value: 80, name: '客户' },
         *    { value: 100, name: '线索' }
         * ]
         *
         */
        List<NameValue> nameValueList = statisticService.loadSaleFunnelData();
        return R.OK(nameValueList);
    }

    @GetMapping(value = "/sourcePie/data")
    public R sourcePieData() {
        /**
         *   [
         *       { value: 1048, name: 'Search Engine' },
         *       { value: 735, name: 'Direct' },
         *       { value: 580, name: 'Email' },
         *       { value: 484, name: 'Union Ads' },
         *       { value: 300, name: 'Video Ads' }
         *   ]
         *
         */
        List<NameValue> nameValueList = statisticService.loadSourcePieData();
        return R.OK(nameValueList);
    }
}
