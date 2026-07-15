package com.example.web.service;

import com.example.web.Bean.TTran;
import com.example.web.query.StatisticsQuery;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TranService {
    List<TTran> getTran (@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    int updataTran(TTran tTran);

    int addTran(TTran tTran);

    StatisticsQuery getStatistics();

    List<TTran> searchTran(TTran tTran);
}
