package com.example.web.Bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户跟踪记录表
 * t_customer_remark
 */
public class TCustomerRemark implements Serializable {
    /**
     * 主键，自动增长，客户备注ID
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 跟踪方式
     */
    private Integer noteWay;

    /**
     * 跟踪内容
     */
    private String noteContent;

    /**
     * 跟踪人
     */
    private Integer createBy;

    /**
     * 跟踪时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 编辑人
     */
    private Integer editBy;

    /**
     * 删除状态（0正常，1删除）
     */
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getNoteWay() {
        return noteWay;
    }

    public void setNoteWay(Integer noteWay) {
        this.noteWay = noteWay;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Integer getEditBy() {
        return editBy;
    }

    public void setEditBy(Integer editBy) {
        this.editBy = editBy;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}