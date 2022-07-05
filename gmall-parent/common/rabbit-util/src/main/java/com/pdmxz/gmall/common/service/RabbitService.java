package com.pdmxz.gmall.common.service;

import com.alibaba.fastjson.JSONObject;
import com.pdmxz.gmall.common.entity.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    public void sendMessage(String exchange,String routingKey,Object msg){

        GmallCorrelationData correlationData = new GmallCorrelationData();
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        String uuid = UUID.randomUUID().toString();
        correlationData.setId(uuid);

        String string = JSONObject.toJSONString(correlationData);
        redisTemplate.opsForValue().set(uuid,string);
        rabbitTemplate.convertAndSend(exchange,routingKey,msg,correlationData);
    }


    public void sendTTLMessage(String exchange,String routingKey,Object msg,int delay){

        GmallCorrelationData correlationData = new GmallCorrelationData();
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        String uuid = UUID.randomUUID().toString();
        correlationData.setId(uuid);

        correlationData.setDelay(true);
        correlationData.setDelayTime(delay);

        String string = JSONObject.toJSONString(correlationData);
        redisTemplate.opsForValue().set(uuid,string);
        rabbitTemplate.convertAndSend(exchange,routingKey,msg,(message) -> {
            message.getMessageProperties().setExpiration(String.valueOf(1000*delay));
            System.out.println("发送时间为"+new Date());
            return message;
        },correlationData);

    }

    public void sendPluginTTLMessage(String exchange,String routingKey,Object msg,int delay){

        GmallCorrelationData correlationData = new GmallCorrelationData();
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        String uuid = UUID.randomUUID().toString();
        correlationData.setId(uuid);

        correlationData.setPluginDelay(true);


        String string = JSONObject.toJSONString(correlationData);
        redisTemplate.opsForValue().set(uuid,string);
        rabbitTemplate.convertAndSend(exchange,routingKey,msg,(message) -> {
            message.getMessageProperties().setDelay(1000*delay);
            System.out.println("发送时间为"+new Date());
            return message;
        },correlationData);

    }


}
