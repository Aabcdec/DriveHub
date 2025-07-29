package com.example.web.Bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TClueAll implements Serializable {
    /**
     * 主键，自动增长，线索ID
     */
    private Integer id;

    /**
     * 线索所属人ID
     */

    private Integer ownerId;

    /**
     * 活动ID
     */

    private Integer activityId;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 称呼
     */
    private Integer appellation;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 微信号
     */
    private String weixin;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 职业
     */
    private String job;

    /**
     * 年收入
     */
    private BigDecimal yearIncome;

    /**
     * 地址
     */
    private String address;

    /**
     * 是否需要贷款（0不需要，1需要）
     */
    private Integer needLoan;

    /**
     * 意向状态
     */
    private Integer intentionState;

    /**
     * 意向产品
     */
    private Integer intentionProduct;

    /**
     * 线索状态
     */
    private Integer state;

    /**
     * 线索来源
     */
    private Integer source;

    /**
     * 线索描述
     */
    private String description;

    /**
     * 下次联系时间
     */
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
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 一对一关联
     */
    private TUser ownerDO = new TUser();
    private TActivity activityDO = new TActivity();
    private TDicValue appellationDO = new TDicValue();
    private TDicValue needLoanDO = new TDicValue();
    private TDicValue intentionStateDO = new TDicValue();
    private TProduct intentionProductDO = new TProduct();
    private TDicValue stateDO = new TDicValue();
    private TDicValue sourceDO = new TDicValue();

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

    public TUser getOwnerDO() {
        return ownerDO;
    }

    public void setOwnerDO(TUser ownerDO) {
        this.ownerDO = ownerDO;
    }

    public TActivity getActivityDO() {
        return activityDO;
    }

    public void setActivityDO(TActivity activityDO) {
        this.activityDO = activityDO;
    }

    public TDicValue getAppellationDO() {
        return appellationDO;
    }

    public void setAppellationDO(TDicValue appellationDO) {
        this.appellationDO = appellationDO;
    }

    public TDicValue getNeedLoanDO() {
        return needLoanDO;
    }

    public void setNeedLoanDO(TDicValue needLoanDO) {
        this.needLoanDO = needLoanDO;
    }

    public TDicValue getIntentionStateDO() {
        return intentionStateDO;
    }

    public void setIntentionStateDO(TDicValue intentionStateDO) {
        this.intentionStateDO = intentionStateDO;
    }

    public TProduct getIntentionProductDO() {
        return intentionProductDO;
    }

    public void setIntentionProductDO(TProduct intentionProductDO) {
        this.intentionProductDO = intentionProductDO;
    }

    public TDicValue getStateDO() {
        return stateDO;
    }

    public void setStateDO(TDicValue stateDO) {
        this.stateDO = stateDO;
    }

    public TDicValue getSourceDO() {
        return sourceDO;
    }

    public void setSourceDO(TDicValue sourceDO) {
        this.sourceDO = sourceDO;
    }

    private static final long serialVersionUID = 1L;
}
