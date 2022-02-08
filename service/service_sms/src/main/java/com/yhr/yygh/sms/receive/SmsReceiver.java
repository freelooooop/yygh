package com.yhr.yygh.sms.receive;

import com.rabbitmq.client.Channel;
import com.yhr.common.rabbit.constant.MqConst;
import com.yhr.yygh.sms.service.SmsService;
import com.yhr.yygh.vo.msm.MsmVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsReceiver {
    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_SMS),
            key = {MqConst.ROUTING_SMS_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        smsService.send(msmVo);
    }
}