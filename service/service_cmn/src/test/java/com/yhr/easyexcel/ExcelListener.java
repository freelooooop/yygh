package com.yhr.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<UserData> {

    //一行一行讀取excel數據
    @Override
    public void invoke(UserData userData, AnalysisContext analysisContext) {
        System.out.println(userData);
    }

    //讀取表頭信息
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表頭信息：" + headMap);
    }

    //讀取之後執行
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
