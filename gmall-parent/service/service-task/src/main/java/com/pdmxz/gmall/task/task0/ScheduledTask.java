package com.pdmxz.gmall.task.task0;

import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSeckillInfo(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_1,"haveToAddInfoFDbTR");
    }
}
