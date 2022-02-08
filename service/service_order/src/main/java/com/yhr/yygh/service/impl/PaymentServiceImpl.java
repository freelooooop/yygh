package com.yhr.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhr.yygh.commom.exception.YyghException;
import com.yhr.yygh.commom.result.ResultCodeEnum;
import com.yhr.yygh.common.helper.HttpRequestHelper;
import com.yhr.yygh.enums.OrderStatusEnum;
import com.yhr.yygh.enums.PaymentStatusEnum;
import com.yhr.yygh.enums.PaymentTypeEnum;
import com.yhr.yygh.hosp.client.HospitalFeignClient;
import com.yhr.yygh.mapper.PaymentMapper;
import com.yhr.yygh.model.order.OrderInfo;
import com.yhr.yygh.model.order.PaymentInfo;
import com.yhr.yygh.service.OrderService;
import com.yhr.yygh.service.PaymentService;
import com.yhr.yygh.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    /**
     * 保存交易记录
     * @param orderInfo
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count >0) return;
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo);
    }

    //更新订单状态
    @Override
    public void paySuccess(String out_trade_no, Map<String, String> paramMap) {
        //根据订单编号得到支付记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",out_trade_no);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        //更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(paramMap.get("transaction_id"));
        paymentInfo.setCallbackContent(paramMap.toString());
        baseMapper.updateById(paymentInfo);
        //根据订单号得到订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        //更新订单信息
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);
        //调用医院接口，更新订单支付信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String,Object> map = new HashMap<>();
        map.put("hoscode",orderInfo.getHoscode());
        map.put("hosRecordId",orderInfo.getHosRecordId());
        map.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(map, signInfoVo.getSignKey());
        map.put("sign", sign);
        JSONObject result = HttpRequestHelper.sendRequest(map, signInfoVo.getApiUrl()+"/order/updatePayStatus");
        if(result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
    }

    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }
}