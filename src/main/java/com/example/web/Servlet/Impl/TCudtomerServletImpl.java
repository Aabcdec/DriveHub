package com.example.web.Servlet.Impl;


import com.example.web.Bean.TClue;
import com.example.web.Bean.TCustomer;
import com.example.web.Mapper.TClueDao;
import com.example.web.Mapper.TCustomerDao;
import com.example.web.Servlet.TCudtomerServlet;
import com.example.web.constant.Constants;
import com.example.web.query.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
//如何异常都回滚
public class TCudtomerServletImpl implements TCudtomerServlet {
    @Resource
    private TCustomerDao tCustomerDao;

    @Resource
    private TClueDao tClueDao;

    @Override
    public List<TCustomer> getCustomer() {
        return tCustomerDao.getCustomer();
    }

    @Override
    public List<marketQuery> getMarketTypes() {
        return tCustomerDao.getMarketTypes();
    }

    @Override
    public int SaveCustomr(TCustomer tCustomer, int createBy) {
        //先验证该用户是否已经转换
        TClue tClue = tClueDao.selectByPrimaryKey(tCustomer.getClueId());
        if(tClue.getState()==-1){
            throw new RuntimeException("该用户已经转为客户，不可再转");
        }
        //向客户表插入一条数据
        //tCustomer已经完成转换
        int insert = tCustomerDao.insertSelective(tCustomer);
        //修改当前线索状态为-1
        TClue tClue1 = new TClue();
        tClue1.setId(tCustomer.getClueId());
        tClue1.setState(-1);
        int updata = tClueDao.updateByPrimaryKeySelective(tClue1);
        return (insert>=1&&updata>=1)?1:0;
    }

    @Override
    public List<TCustomer> selectCustomerPage(Integer pageNum, Integer pageSize) {
        //起始页等于页码减一*页面大小
        pageNum=(pageNum-1)*pageSize;
        return tCustomerDao.selectCustomerPage(pageNum,pageSize);
    }

    @Override
    public List<CustomerExcel> getCustomerByExcel(List<String> idList) {
        List<CustomerExcel> customerExcelList = new ArrayList<>();

        List<TCustomer> tCustomerList = tCustomerDao.selectCustomerByExcel(idList);

        //把从数据库查询出来的List<TCustomer>数据，转换为 List<CustomerExcel>数据
        tCustomerList.forEach(tCustomer -> {
            CustomerExcel customerExcel = new CustomerExcel();

            //需要一个一个设置，没有办法，因为没法使用BeanUtils复制
            customerExcel.setOwnerName(ObjectUtils.isEmpty(tCustomer.getOwnerDO()) ? Constants.EMPTY : tCustomer.getOwnerDO().getName());
            customerExcel.setActivityName(ObjectUtils.isEmpty(tCustomer.getActivityDO()) ? Constants.EMPTY : tCustomer.getActivityDO().getName());
            customerExcel.setFullName(tCustomer.getClueDO().getFullName());
            customerExcel.setAppellationName(ObjectUtils.isEmpty(tCustomer.getAppellationDO()) ? Constants.EMPTY : tCustomer.getAppellationDO().getTypeValue());
            customerExcel.setPhone(tCustomer.getClueDO().getPhone());
            customerExcel.setWeixin(tCustomer.getClueDO().getWeixin());
            customerExcel.setQq(tCustomer.getClueDO().getQq());
            customerExcel.setEmail(tCustomer.getClueDO().getEmail());
            customerExcel.setAge(tCustomer.getClueDO().getAge());
            customerExcel.setJob(tCustomer.getClueDO().getJob());
            customerExcel.setYearIncome(tCustomer.getClueDO().getYearIncome());
            customerExcel.setAddress(tCustomer.getClueDO().getAddress());
            customerExcel.setNeedLoadName(ObjectUtils.isEmpty(tCustomer.getNeedLoanDO()) ? Constants.EMPTY : tCustomer.getNeedLoanDO().getTypeValue());
            customerExcel.setProductName(ObjectUtils.isEmpty(tCustomer.getIntentionProductDO()) ? Constants.EMPTY : tCustomer.getIntentionProductDO().getName());
            customerExcel.setSourceName(ObjectUtils.isEmpty(tCustomer.getSourceDO()) ? Constants.EMPTY : tCustomer.getSourceDO().getTypeValue());
            customerExcel.setDescription(tCustomer.getDescription());
            customerExcel.setNextContactTime(tCustomer.getNextContactTime());

            customerExcelList.add(customerExcel);
        });
        return customerExcelList;
    }

    @Override
    public List<TCustomer> BysearchCustomer(searchCustomerQuery searchCustomerQuery) {
        return tCustomerDao.BysearchCustomer(searchCustomerQuery);
    }

    @Override
    public int updataCustomer(Long id, CustomerForm customerForm) {
        TClue tClue = new TClue();
        tClue.setAddress(customerForm.getAddress());
        tClue.setEmail(customerForm.getEmail());
        if(customerForm.getCreateBy().equals("管理员")){
            tClue.setCreateBy(1);
        } else if (customerForm.getCreateBy().equals("于嫣")) {
            tClue.setCreateBy(2);
        } else if (customerForm.getCreateBy().equals("张琪")) {
            tClue.setCreateBy(3);
        } else if (customerForm.getCreateBy().equals("苏蜿婷")) {
            tClue.setCreateBy(4);
        } else if (customerForm.getCreateBy().equals("吴潇潇")) {
            tClue.setCreateBy(5);
        }else{
            tClue.setCreateBy(0);
        }
        if(customerForm.getStatus().equals("有意向")){
            tClue.setState(1);
        } else if (customerForm.getStatus().equals("无意向")) {
            tClue.setState(2);
        }
        else if (customerForm.getStatus().equals("意向不明")) {
            tClue.setState(3);
        }else{
            tClue.setState(0);
        }
        tClue.setFullName(customerForm.getName());
        tClue.setPhone(customerForm.getPhone());
        tClue.setId(customerForm.getClueId());
        System.out.println(tClue.toString());
        int i = tClueDao.updateByPrimaryKeySelective(tClue);
        System.out.println(i);
        return tCustomerDao.updataCustomer(id,customerForm);
    }

    @Override
    public List<Long> getCustomerId() {
        return tCustomerDao.getCustomerId();
    }


}
