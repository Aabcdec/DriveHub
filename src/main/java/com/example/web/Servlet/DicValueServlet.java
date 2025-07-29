package com.example.web.Servlet;

import com.example.web.Bean.TDicValue;
import com.example.web.Result.R;

public interface DicValueServlet {
     R getValueByTyoeCode(Integer pageNum,Integer pageSize,String typeCode);
     int addByTypeAddValue(TDicValue tDicValue);
     int deleteByIdValue(Integer id);
    int PutByTypeAddValue(TDicValue tDicValue);
}
