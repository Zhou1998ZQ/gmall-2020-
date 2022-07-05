package com.pdmxz.gmall.list.listener;

import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.list.controller.ListApiController;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ListListener{

    @Autowired
    private ListApiController listApiController;

    @RabbitListener(queues = MqConst.QUEUE_GOODS_UPPER)
    private void onSale(Long skuId ,Message message, Channel channel){

        try {
//            String skuId = new String(message.getBody());
//            listApiController.onSale(Long.parseLong(skuId));
//            System.out.println(skuId);
            listApiController.onSale(skuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if (!message.getMessageProperties().getRedelivered()){
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

    @RabbitListener(queues = MqConst.QUEUE_GOODS_LOWER)
    private void onCancel(Long skuId ,Message message, Channel channel){

        try {
//            String skuIds = new String(message.getBody());
//            long skuId = Long.parseLong(skuIds);
//            System.out.println(skuId);
            listApiController.onCancel(skuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if (!message.getMessageProperties().getRedelivered()){
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
