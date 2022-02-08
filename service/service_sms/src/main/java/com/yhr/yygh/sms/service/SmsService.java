package com.yhr.yygh.sms.service;

import com.yhr.yygh.vo.msm.MsmVo;

public interface SmsService {
    boolean send(String phone, String code);
    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
