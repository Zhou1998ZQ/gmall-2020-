package com.pdmxz.gmall.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class ConsumerListener {

    @RabbitListener(queues = "q1")
    public void consume(String msg, Channel channel, Message message) {
        try {
            System.out.println("消费者接受到byString" + msg);
            System.out.println("消费者接受到byMessage" + new String(message.getBody()));
            //手动应答 参数1：消息唯一标识符 参数2：true为批量应答 false为单个应答

            int i = 1 / 0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            //第一次从队列重新取值
            if (!message.getMessageProperties().getRedelivered()){
                try {
                    System.out.println("第一次从队列重新取值");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else {
                //第二次从队列重新取值
                try {
                    log.error("消息消费异常"+new String(message.getBody()));
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }


        }
    }
    @RabbitListener(queues = "queue")
    public void consumeTTL(Message message,String msg,Channel channel){

        try {
            System.out.println("消费者接受到byString" + msg +"--------"+new Date());
            System.out.println("消费者接受到byMessage" + new String(message.getBody()));
            //手动应答 参数1：消息唯一标识符 参数2：true为批量应答 false为单个应答
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //第一次从队列重新取值
            if (!message.getMessageProperties().getRedelivered()){
                try {
                    System.out.println("第一次从队列重新取值");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else {
                //第二次从队列重新取值
                try {
                    log.error("消息消费异常"+new String(message.getBody()));
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }


        }
    }


    @RabbitListener(queues = "pluginQueue")
    public void consumePlugin(String msg, Channel channel, Message message) {
        try {
            System.out.println("消费者接受到byString" + msg);
            System.out.println("消费者接受到byMessage" + new String(message.getBody()));
            //手动应答 参数1：消息唯一标识符 参数2：true为批量应答 false为单个应答


            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            //第一次从队列重新取值
            if (!message.getMessageProperties().getRedelivered()){
                try {
                    System.out.println("第一次从队列重新取值");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else {
                //第二次从队列重新取值
                try {
                    log.error("消息消费异常"+new String(message.getBody()));
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }


        }
    }
}
