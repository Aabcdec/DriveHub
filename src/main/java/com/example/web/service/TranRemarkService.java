package com.example.web.service;

import com.example.web.Bean.TTranRemark;

import java.util.List;

public interface TranRemarkService {

    int saveRemark(TTranRemark remark);

    List<TTranRemark> getByIdList(Integer tranId);
}
