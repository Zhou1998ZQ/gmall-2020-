package com.pdmxz.gmall.order.listener;

import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.model.enums.OrderStatus;
import com.pdmxz.gmall.model.enums.ProcessStatus;
import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.order.mapper.OrderInfoMapper;
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
public class OrderListener {

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void consumeOrder(Long orderId, Channel channel, Message message) {
        try {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo != null && OrderStatus.UNPAID.name().equals(orderInfo.getOrderStatus())){
            orderInfo.setOrderStatus(OrderStatus.CLOSED.name());
            orderInfo.setProcessStatus(ProcessStatus.CLOSED.name());
            orderInfoMapper.updateById(orderInfo);
        }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if (!message.getMessageProperties().getRedelivered()){
                //重新获取队列中的消息
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else {
                try {
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY,autoDelete = "false",declare = "true")
            ,exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),key = MqConst.ROUTING_PAYMENT_PAY))
    public void consumeToChangeOrderState(Channel channel,Long orderId,Message message){
        try {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo != null && ProcessStatus.UNPAID.name().equals(orderInfo.getProcessStatus())){
            orderInfo.setProcessStatus(ProcessStatus.PAID.name());
            orderInfo.setOrderStatus(OrderStatus.PAID.name());
            orderInfoMapper.updateById(orderInfo);
        }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            if (!message.getMessageProperties().getRedelivered()){
                //重新获取队列中的消息
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else {
                try {
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
