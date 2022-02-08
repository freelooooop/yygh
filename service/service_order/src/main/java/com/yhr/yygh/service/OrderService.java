package com.yhr.yygh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhr.yygh.model.order.OrderInfo;
import com.yhr.yygh.model.user.UserInfo;
import com.yhr.yygh.vo.order.OrderCountQueryVo;
import com.yhr.yygh.vo.order.OrderQueryVo;

import java.util.Map;

public interface OrderService extends IService<OrderInfo> {
    //保存订单
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> page1, OrderQueryVo orderQueryVo);

    Boolean cancelOrder(Long orderId);

    void patientTips();

    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

}