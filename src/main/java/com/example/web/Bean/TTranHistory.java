package com.example.web.Bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易历史记录表
 * t_tran_history
 */
public class TTranHistory implements Serializable {
    /**
     * 主键，自动增长，交易记录ID
     */
    private Integer id;

    /**
     * 交易ID
     */
    private Integer tranId;

    /**
     * 交易阶段
     */
    private Integer stage;

    /**
     * 交易金额
     */
    private BigDecimal money;

    /**
     * 交易预计成交时间
     */
    private Date expectedDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }
}