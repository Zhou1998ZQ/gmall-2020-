package com.pdmxz.gmall.user.service;

import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.model.user.UserInfo;

import java.util.List;

public interface UserInfoService {
    UserInfo checkLoginByUserInfo(UserInfo userInfo);

    List<UserAddress> getUserAddress(String userId);
}
