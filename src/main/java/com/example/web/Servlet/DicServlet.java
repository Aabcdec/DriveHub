package com.example.web.Servlet;

import com.example.web.Bean.TDicType;
import com.example.web.Bean.TDicValue;
import com.example.web.Result.R;

import java.util.List;

public interface DicServlet {
    R getAllType(Integer pageNum,Integer pageSize,String searchKey);
    int updateTypeData(TDicType tDicType);
    int DeleteTypeData(Integer id);
    int addTypeData(TDicType tDicType);
}
