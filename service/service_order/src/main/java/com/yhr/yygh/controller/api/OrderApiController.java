package com.yhr.yygh.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.commom.utils.AuthContextHolder;
import com.yhr.yygh.enums.OrderStatusEnum;
import com.yhr.yygh.model.order.OrderInfo;
import com.yhr.yygh.service.OrderService;
import com.yhr.yygh.vo.order.OrderCountQueryVo;
import com.yhr.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId,
            @ApiParam(name = "patientId", value = "就诊人id", required = true)
            @PathVariable Long patientId) {
        return Result.ok(orderService.saveOrder(scheduleId, patientId));
    }

    //根据订单id查询订单详情
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    //订单列表接口(条件查询带分页)
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request){
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> page1 = new Page<>(page,limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(page1,orderQueryVo);
        return Result.ok(pageModel);
    }

    //获取订单状态
    @GetMapping("auth/getStatusList")
    public Result getStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    //取消预约
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        Boolean isCancel = orderService.cancelOrder(orderId);
        return Result.ok(isCancel);
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }
}
