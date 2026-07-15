package com.example.web.service.impl;

import com.example.web.Bean.TTranRemark;
import com.example.web.Mapper.TTranRemarkDao;
import com.example.web.service.TranRemarkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranRemarkServiceImpl implements TranRemarkService {
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
