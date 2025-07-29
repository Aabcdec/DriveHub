package com.example.web.manager;

import com.example.web.Mapper.TActivityDao;
import com.example.web.Mapper.TClueDao;
import com.example.web.Mapper.TCustomerDao;
import com.example.web.Mapper.TTranDao;
import com.example.web.Result.NameValue;
import com.example.web.Result.R;
import com.example.web.Result.SummaryData;
import com.example.web.query.MonthLineData;
import com.example.web.query.YearLineData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatisticManager {

    @Resource
    private TActivityDao tActivitydao;

    @Resource
    private TClueDao tCluedao;

    @Resource
    private TCustomerDao tCustomerdao;

    @Resource
    private TTranDao tTrandao;

    public SummaryData loadSummaryData() {
        //有效的市场活动总数
        Integer effectiveActivityCount = tActivitydao.selecOngoingActivity(); //偷懒了一下

        //总的市场活动数
        Integer totalActivityCount = tActivitydao.selectByCount();

        //线索总数
        Integer totalClueCount = tCluedao.selectClueByCount();

        //客户总数
        Integer totalCustomerCount = tCustomerdao.selectByCount();

        //成功的交易额
        BigDecimal successTranAmount = tTrandao.selectBySuccessTranAmount();

        //总的交易额（包含成功和不成功的）
        BigDecimal totalTranAmount = tTrandao.selectByTotalTranAmount();

        return SummaryData.builder()
                .effectiveActivityCount(effectiveActivityCount)
                .totalActivityCount(totalActivityCount)
                .totalClueCount(totalClueCount)
                .totalCustomerCount(totalCustomerCount)
                .successTranAmount(successTranAmount)
                .totalTranAmount(totalTranAmount)
                .build();
    }

    public List<NameValue> loadSaleFunnelData() {
        List<NameValue> resultList = new ArrayList<>();

        /**
         * [
         *    { value: 20, name: '成交' },
         *    { value: 60, name: '交易' },
         *    { value: 80, name: '客户' },
         *    { value: 100, name: '线索' }
         * ]
         *
         */
        int clueCount = tCluedao.selectClueByCount();
        int customerCount = tCustomerdao.selectByCount();
        int tranCount = tTrandao.selectByTotalTranCount();
        int tranSuccessCount = tTrandao.selectBySuccessTranCount();

        NameValue clue = NameValue.builder().name("线索").value(clueCount).build();
        resultList.add(clue);

        NameValue customer = NameValue.builder().name("客户").value(customerCount).build();
        resultList.add(customer);

        NameValue tran = NameValue.builder().name("交易").value(tranCount).build();
        resultList.add(tran);

        NameValue tranSuccess = NameValue.builder().name("成交").value(tranSuccessCount).build();
        resultList.add(tranSuccess);

        return resultList;
    }

    public List<NameValue> loadSourcePieData() {
        return tCluedao.selectBySource();
    }

    public List<YearLineData> getByYearLineDate() {
        return tCluedao.getByYearLineDate();
    }

    public List<MonthLineData> getByMouthLineDate() {
        return tCluedao.getByMouthLineDate();
    }

    public List<YearLineData> getByYearLineXConverterDate() {
        return tCluedao.getByYearLineXConverterDate();
    }

    public List<MonthLineData> getByMouthLineConveterData() {
        return tCluedao.getByMouthLineConveterData();
    }
}
