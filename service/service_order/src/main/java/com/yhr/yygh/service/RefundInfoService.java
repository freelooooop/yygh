package com.yhr.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhr.yygh.model.order.PaymentInfo;
import com.yhr.yygh.model.order.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {
    /**
     * 保存退款记录
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}