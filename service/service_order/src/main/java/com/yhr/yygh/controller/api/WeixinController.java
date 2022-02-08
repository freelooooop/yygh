package com.yhr.yygh.controller.api;

import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.service.PaymentService;
import com.yhr.yygh.service.WeixinService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {
    @Autowired
    private WeixinService weixinPayService;
    @Autowired
    private PaymentService paymentService;
    /**
     * 下单 生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public Result createNative(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(weixinPayService.createNative(orderId));
    }

    //查询支付状态
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        //调用微信接口
        Map<String,String> map = weixinPayService.queryPayStatus(orderId);
        //判断
        if (map == null){
            return Result.fail().message("pay error");
        }
        if ("SUCCESS".equals(map.get("trade_state"))){
            //更新订单信息
            String out_trade_no = map.get("out_trade_no");
            paymentService.paySuccess(out_trade_no,map);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }
}