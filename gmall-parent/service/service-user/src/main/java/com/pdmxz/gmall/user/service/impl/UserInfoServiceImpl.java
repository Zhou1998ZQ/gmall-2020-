package com.pdmxz.gmall.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.common.util.MD5;
import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.model.user.UserInfo;
import com.pdmxz.gmall.user.mapper.UserAddressMapper;
import com.pdmxz.gmall.user.mapper.UserInfoMapper;
import com.pdmxz.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;
    //登录
    @Override
    public UserInfo checkLoginByUserInfo(UserInfo userInfo) {

       return userInfoMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("login_name",userInfo.getLoginName())
                .eq("passwd", MD5.encrypt(userInfo.getPasswd())));

    }

    @Override
    public List<UserAddress> getUserAddress(String userId) {
        return userAddressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id",userId));
    }
}
