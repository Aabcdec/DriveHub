package com.example.web.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class ActiveProductQuery {
    private Integer id;
    /**
     * 活动所属人ID
     */
    private Integer ownerId;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 活动预算
     */
    private BigDecimal cost;
    /**
     * 活动描述
     */
    private String description;
    /**
     * 活动创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 活动创建人
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Integer createBy;
    /**
     * 活动编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date editTime;
    /**
     * 活动编辑人
     */
    private Integer editBy;
    private static final long serialVersionUID = 1L;
    private String userName;
    private String image;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;
    private int party;
    private int productId;
    private String productName;
    private Double quotation;
    private String active;
    private String activeType;
    private Integer total;

}
