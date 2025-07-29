package com.example.web.Bean;

import java.io.Serializable;

/**
 * 字典值表
 * t_dic_value
 */
public class TDicValue implements Serializable {
    /**
     * 主键，自动增长，字典值ID
     */
    private Integer id;

    /**
     * 字典类型代码
     */
    private String typeCode;

    /**
     * 字典值
     */
    private String typeValue;

    /**
     * 字典值排序
     */
    private Integer order;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}