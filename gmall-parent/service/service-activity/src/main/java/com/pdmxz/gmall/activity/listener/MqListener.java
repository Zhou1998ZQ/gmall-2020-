package com.pdmxz.gmall.activity.listener;

import com.pdmxz.gmall.activity.service.ActivityInfoService;
import com.pdmxz.gmall.common.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MqListener {


    @Autowired
    private ActivityInfoService activityInfoService;


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_TASK_1,autoDelete = "false",durable = "true")
            ,exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),key = MqConst.ROUTING_TASK_1))
    public void consumeMessage(Message message, Channel channel,String msg){
        activityInfoService.sendActivityInfoToRedis();
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
