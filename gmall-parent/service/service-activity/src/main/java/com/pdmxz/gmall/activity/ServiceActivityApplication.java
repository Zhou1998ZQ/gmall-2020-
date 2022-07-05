package com.pdmxz.gmall.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.pdmxz.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.pdmxz.gmall"})
public class ServiceActivityApplication {

   public static void main(String[] args) {
      SpringApplication.run(ServiceActivityApplication.class, args);
   }
}