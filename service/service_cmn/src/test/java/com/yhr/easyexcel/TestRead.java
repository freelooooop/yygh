package com.yhr.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        //讀取的文件路徑
        String fileName = "D:\\excel\\01.xlsx";

        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
