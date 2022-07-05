package com.pdmxz.gmall.user.feign;

import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.user.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient(name = "service-user",fallback = UserFeignClientFallback.class)
public interface UserFeignClient {



    @GetMapping("/api/user/passport/getUserAddress")
    List<UserAddress> getUserAddress();
}
