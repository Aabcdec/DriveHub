package com.example.web.Servlet;

import com.example.web.Bean.TTranRemark;

import java.util.List;

public interface TranRemarkServelt {

    int saveRemark(TTranRemark remark);

    List<TTranRemark> getByIdList(Integer tranId);
}
