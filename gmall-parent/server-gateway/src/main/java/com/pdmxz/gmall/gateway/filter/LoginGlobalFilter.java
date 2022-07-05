package com.pdmxz.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.common.result.ResultCodeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class LoginGlobalFilter implements GlobalFilter, Ordered {
    public static final String TOKEN = "token";
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    public static final String LOGINURL = "http://passport.gmall.com/login.html?originUrl=";
    public static final String USERTEMPID = "userTempId";
    //用于路径匹配
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${url.all}")
    private String[] urls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String userId = getUserId(request);
        //同步的，且需要用户登录
        for (String url : urls) {
            if (path.indexOf(url) != -1 && StringUtils.isEmpty(userId)){
                try {
                    //重定向到登录页面
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().add(HttpHeaders.LOCATION,LOGINURL+URLEncoder.encode(request.getURI().toString(),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return response.setComplete();
            }
        }

        //异步 如 /api/xx/auth/xx/xx.html
        if (antPathMatcher.match("/**/auth/**",path) && StringUtils.isEmpty(userId)){
            return out(response,ResultCodeEnum.LOGIN_AUTH);
        }
        //访问对部资源
        if (antPathMatcher.match("/**/inner/**",path)){
            return out(response,ResultCodeEnum.PERMISSION);
        }

        //分发userId或userTempId
        //传递真实用户ID
        request.mutate().header("userId",userId);
        //传递临时用户ID
        String userTempId = getUserTempId(request);
        request.mutate().header("userTempId",userTempId);
        return chain.filter(exchange);
    }

    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = request.getHeaders().getFirst(USERTEMPID);
        if (StringUtils.isEmpty(userTempId)){
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            if (cookies!=null){
                HttpCookie cookie = cookies.getFirst(USERTEMPID);
                if (cookie!=null){
                    userTempId = cookie.getValue();
                }
            }
        }
        return userTempId;
    }

    public Mono<Void> out(ServerHttpResponse serverHttpResponse, ResultCodeEnum resultCodeEnum){
        Result<Object> result = Result.build(null, resultCodeEnum);

        String jsonString = JSONObject.toJSONString(result);

        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        DataBuffer dataBuffer = dataBufferFactory.wrap(jsonString.getBytes());
        serverHttpResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_UTF8_VALUE);
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
    }


    public String getUserId(ServerHttpRequest request) {
        //token在cookie中或在header中
        String token = "";
        if (request != null){
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            if (cookies != null){
                HttpCookie cookie = cookies.getFirst(TOKEN);
                if (cookie != null){
                    token = cookie.getValue();
                }
            }
        }


        if (StringUtils.isEmpty(token)) {
            token = request.getHeaders().getFirst(TOKEN);
        }
        //判断token是否有效
        if (!StringUtils.isEmpty(token)){
            if (redisTemplate.hasKey(USER_LOGIN_KEY_PREFIX + token)){
                //有效返回userId
                return (String) redisTemplate.opsForValue().get(USER_LOGIN_KEY_PREFIX + token);
            }
        }
        //无效返回null
        return null;
    }

    /**
     * gateway中有默认9大过滤器
     * 默认为0
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
