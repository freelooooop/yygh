package com.yhr.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserData {

    @ExcelProperty(value = "用戶編號",index = 0)
    private int uid;

    @ExcelProperty(value = "用戶名稱",index = 1)
    private String username;
}
