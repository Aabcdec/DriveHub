package com.example.web.Servlet.Impl;

import com.example.web.Bean.TTranRemark;
import com.example.web.Mapper.TTranRemarkDao;
import com.example.web.Servlet.TranRemarkServelt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranRemarkServeltImpl implements TranRemarkServelt {
    @Resource
    private TTranRemarkDao tTranRemarkDao;
    @Override
    public int saveRemark(TTranRemark remark) {
        return tTranRemarkDao.insertSelective(remark);
    }

    @Override
    public List<TTranRemark> getByIdList(Integer tranId) {
        return tTranRemarkDao.getByIdList(tranId);
    }
}
