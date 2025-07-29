package com.example.web.Bean;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.example.web.converter.*;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 线索表
 * t_clue
 */
@ExcelIgnoreUnannotated  //没有加注解的字段就不参与Excel的读写
public class TClue implements Serializable {
    /**
     * 主键，自动增长，线索ID
     */
    private Integer id;

    /**
     * 线索所属人ID
     */
    @ExcelProperty(value = "负责人")
    private Integer ownerId;

    /**
     * 活动ID
     */
    @ExcelProperty(value = "所属活动")
    private Integer activityId;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String fullName;

    /**
     * 称呼
     */
    @ExcelProperty(value = "称呼",converter = AppellationConverter.class)
    private Integer appellation;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 微信号
     */
    @ExcelProperty(value = "微信号")
    private String weixin;

    /**
     * QQ号
     */
    @ExcelProperty(value = "QQ号")
    private String qq;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 年龄
     */
    @ExcelProperty(value = "年龄")
    private Integer age;

    /**
     * 职业
     */
    @ExcelProperty(value = "职业")
    private String job;

    /**
     * 年收入
     */
    @ExcelProperty(value = "年收入")
    private BigDecimal yearIncome;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 是否需要贷款（0不需要，1需要）
     */
    @ExcelProperty(value = "是否贷款" ,converter = NeedLoanConverter.class)
    private Integer needLoan;

    /**
     * 意向状态
     */
    @ExcelProperty(value = "意向状态",converter = IntentionStateConverter.class)
    private Integer intentionState;

    /**
     * 意向产品
     */
    @ExcelProperty(value = "意向产品",converter = IntentionProductConverter.class)
    private Integer intentionProduct;

    /**
     * 线索状态
     */
    @ExcelProperty(value = "线索状态",converter = StateConverter.class)
    private Integer state;

    /**
     * 线索来源
     */
    @ExcelProperty(value = "线索来源",converter = SourceConverter.class)
    private Integer source;

    /**
     * 线索描述
     */
    @ExcelProperty(value = "线索描述")
    private String description;

    /**
     * 下次联系时间
     */
    @ExcelProperty(value = "下次联系时间")
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
    private int total;
    private  String  activityName;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 编辑人
     */
    private Integer editBy;

    private static final long serialVersionUID = 1L;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAppellation() {
        return appellation;
    }

    public void setAppellation(Integer appellation) {
        this.appellation = appellation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public BigDecimal getYearIncome() {
        return yearIncome;
    }

    public void setYearIncome(BigDecimal yearIncome) {
        this.yearIncome = yearIncome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNeedLoan() {
        return needLoan;
    }

    public void setNeedLoan(Integer needLoan) {
        this.needLoan = needLoan;
    }

    public Integer getIntentionState() {
        return intentionState;
    }

    public void setIntentionState(Integer intentionState) {
        this.intentionState = intentionState;
    }

    public Integer getIntentionProduct() {
        return intentionProduct;
    }

    public void setIntentionProduct(Integer intentionProduct) {
        this.intentionProduct = intentionProduct;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getNextContactTime() {
        return nextContactTime;
    }

    public void setNextContactTime(Date nextContactTime) {
        this.nextContactTime = nextContactTime;
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

    @Override
    public String toString() {
        return "TClue{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", activityId=" + activityId +
                ", fullName='" + fullName + '\'' +
                ", appellation=" + appellation +
                ", phone='" + phone + '\'' +
                ", weixin='" + weixin + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                ", yearIncome=" + yearIncome +
                ", address='" + address + '\'' +
                ", needLoan=" + needLoan +
                ", intentionState=" + intentionState +
                ", intentionProduct=" + intentionProduct +
                ", state=" + state +
                ", source=" + source +
                ", description='" + description + '\'' +
                ", nextContactTime=" + nextContactTime +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                ", editTime=" + editTime +
                ", total=" + total +
                ", activityName='" + activityName + '\'' +
                ", editBy=" + editBy +
                '}';
    }
}