package com.pdmxz.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;


/**
 * 无实例化
 * //1:当前请求   任意位置如何获取当前请求对象
 *         ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)
 *                 RequestContextHolder.getRequestAttributes();
 *         if(null != servletRequestAttributes){
 *             HttpServletRequest request = servletRequestAttributes.getRequest();
 *             if(null != request){
 *                 String userId = request.getHeader("userId");
 *                 String userTempId = request.getHeader("userTempId");
 *                 if(!StringUtils.isEmpty(userId)){
 *                     //2：马上发出的远程调用请求
 *                     requestTemplate.header("userId",userId);
 *                 }
 *                 if(!StringUtils.isEmpty(userTempId)){
 *                     //2：马上发出的远程调用请求
 *                     requestTemplate.header("userTempId",userTempId);
 *                 }
 *             }
 */
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        //当前请求 网关->微服务A
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)
                              RequestContextHolder.getRequestAttributes();
        if(null != servletRequestAttributes){
             HttpServletRequest request = servletRequestAttributes.getRequest();
              if(null != request){
                  String userId = request.getHeader("userId");
                  String userTempId = request.getHeader("userTempId");
                  if(!StringUtils.isEmpty(userId)){
                      //远程调用请求
                      requestTemplate.header("userId",userId);
                  }
                  if(!StringUtils.isEmpty(userTempId)){
                      //远程调用请求
                      requestTemplate.header("userTempId",userTempId);
                  }
              }
        }
    }
}
