package com.yhr.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhr.yygh.model.hosp.HospitalSet;
import com.yhr.yygh.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
