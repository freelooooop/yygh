package com.yhr.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhr.yygh.model.order.OrderInfo;
import com.yhr.yygh.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    /**
     * 保存交易记录
     * @param order
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);

    void paySuccess(String out_trade_no, Map<String, String> map);

    //获取支付记录
    PaymentInfo getPaymentInfo(Long orderId,Integer paymentType);
}