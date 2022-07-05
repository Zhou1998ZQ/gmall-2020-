package com.pdmxz.gmall.user.controller;


import com.pdmxz.gmall.common.constant.RedisConst;
import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.common.util.AuthContextHolder;
import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.model.user.UserInfo;
import com.pdmxz.gmall.user.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/passport")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate redisTemplate;


    //登录
    @PostMapping("/login")
    public Result checkLogin(@RequestBody UserInfo userInfo){
        //判断userInfo是否为Null已经用户名和密码是否为空
        if (userInfo != null || !StringUtils.isEmpty(userInfo.getLoginName()) ||!StringUtils.isEmpty(userInfo.getPasswd())){
            userInfo = userInfoService.checkLoginByUserInfo(userInfo);
            //当数据库有该值时
            if (userInfo != null){
                String token = UUID.randomUUID().toString();
                //存缓存
                redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX+token,userInfo.getId().toString(),RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
                //将NickName和Token送给前端
                Map map = new HashMap();
                map.put("token",token);
                map.put("nickName",userInfo.getNickName());
                return Result.ok(map);
            }else {
                return Result.fail().message("用户名或密码错误");
            }
        }else {
            return Result.fail().message("请输入用户名或密码");
        }

    }

    //根据userId查询地址集合
    @GetMapping("/getUserAddress")
    public List<UserAddress> getUserAddress(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        return userInfoService.getUserAddress(userId);
    }
}
