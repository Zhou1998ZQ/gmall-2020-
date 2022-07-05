package com.pdmxz.gmall.user.feign.fallback;

import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.user.feign.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public List<UserAddress> getUserAddress() {
        return null;
    }
}
