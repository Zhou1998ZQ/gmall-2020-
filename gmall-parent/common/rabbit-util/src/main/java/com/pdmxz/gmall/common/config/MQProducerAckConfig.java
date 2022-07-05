package com.pdmxz.gmall.common.config;

import com.alibaba.fastjson.JSONObject;
import com.pdmxz.gmall.common.entity.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    // MQProducerAckConfig需要等rabbitTemplate初始化好了后
    //Constructor >> @Autowired >> @PostConstruct
    @PostConstruct
    public void afterInit(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    //交换机应答  接收到或没接受都会响应
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        if (ack){
            //接收到了消息
            log.info("交换机接收到了消息");
        }else {
            //没接收到消息
            log.error("交换机没有接收到消息");
            retrySend(correlationData);
        }
    }

    private void retrySend(CorrelationData correlationData) {

        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
        if (gmallCorrelationData.getRetryCount() < 2){
            String exchange = gmallCorrelationData.getExchange();
            Object message = gmallCorrelationData.getMessage();
            String routingKey = gmallCorrelationData.getRoutingKey();
            int retryCount = gmallCorrelationData.getRetryCount();
            retryCount++;
            gmallCorrelationData.setRetryCount(retryCount);
            String id = correlationData.getId();
            String string = JSONObject.toJSONString(gmallCorrelationData);
            redisTemplate.opsForValue().set(id,string);
            log.warn("生产者第"+retryCount+"次重复发送消息");
            rabbitTemplate.convertAndSend(exchange,routingKey,message,gmallCorrelationData);
        }else {
            log.error("请联系工作人员");
        }

    }

    //队列应答 不响应说明接受到了消息
    //(Body:'ha' MessageProperties [headers={spring_returned_message_correlation=410056c0-1aed-4440-ac7f-d9f698b6db81}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

        String uuid = message.getMessageProperties().getHeader("spring_returned_message_correlation");
        String o = (String) redisTemplate.opsForValue().get(uuid);
        GmallCorrelationData gmallCorrelationData = JSONObject.parseObject(o, GmallCorrelationData.class);
        if (gmallCorrelationData.isPluginDelay()){
            return;
        }
        retrySend(gmallCorrelationData);

        log.error("队列没获取到消息"+message);
        log.error("队列没获取到消息码"+replyCode);
        log.error("队列没获取到消息原因"+replyText);
        log.error("队列没获取到消息交换机"+exchange);
        log.error("队列没获取到消息路由Key"+routingKey);

    }
}
