package com.example.web.Bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 市场活动备注表
 * t_activity_remark
 */
public class TActivityRemark implements Serializable {
    /**
     * 主键，自动增长，活动备注ID
     */
    private Integer id;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 备注内容
     */
    private String noteContent;

    /**
     * 备注创建时间
     */
    private Date createTime;

    /**
     * 备注创建人
     */
    private Integer createBy;

    /**
     * 备注编辑时间
     */
    private Date editTime;

    /**
     * 备注编辑人
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

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
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