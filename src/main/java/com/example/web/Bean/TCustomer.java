package com.example.web.Bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户表
 * t_customer
 */
@Data
public class TCustomer implements Serializable {
    /**
     * 主键，自动增长，客户ID
     */
    private Integer id;

    /**
     * 线索ID
     */
    private Integer clueId;

    /**
     * 选购产品
     */
    private Integer product;

    /**
     * 客户描述
     */
    private String description;

    /**
     * 下次联系时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextContactTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 编辑人
     */
    private Integer editBy;
    private Integer total;
    private BigDecimal yearIncome;
    private static final long serialVersionUID = 1L;
    /**
     * 一对一关联8个对象
     */
    private TClue clueDO = new TClue();
    private TUser ownerDO = new TUser();
    private TActivity activityDO = new TActivity();
    private TDicValue appellationDO = new TDicValue();
    private TDicValue needLoanDO = new TDicValue();
    private TDicValue intentionStateDO = new TDicValue();
    private TProduct intentionProductDO = new TProduct();
    private TDicValue stateDO = new TDicValue();
    private TDicValue sourceDO = new TDicValue();

}