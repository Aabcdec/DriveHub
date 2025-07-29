package com.example.web.query;

import lombok.Data;

@Data
public class StatisticsQuery {
    private Integer totalTransactions;
    private Integer completedTransaction;
    private Integer pendingTransactions;
    private Integer overdueTransactions;
}
