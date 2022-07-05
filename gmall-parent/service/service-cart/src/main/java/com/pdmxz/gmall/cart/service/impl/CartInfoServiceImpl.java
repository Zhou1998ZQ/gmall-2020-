package com.pdmxz.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.cart.mapper.CartInfoMapper;
import com.pdmxz.gmall.cart.service.CartInfoAsycService;
import com.pdmxz.gmall.cart.service.CartInfoService;
import com.pdmxz.gmall.common.constant.RedisConst;
import com.pdmxz.gmall.common.util.AuthContextHolder;
import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.model.cart.CartInfo;
import com.pdmxz.gmall.model.product.SkuInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private CartInfoAsycService cartInfoAsycService;

    @Override
    public void addToCart(Long skuId, Integer skuNum) {

        //获取userId或UserTempId 拦截器 FeignInterceptor
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId=AuthContextHolder.getUserTempId(request);
        }

        System.out.println(userId);

        // Map userId = new HashMap();
        // skuId.put(skuId,cartInfo);
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(cacheKey, skuId.toString());
        if (cartInfo == null){
            //当缓存没有，第一次添加
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            CartInfo cartInfo1  = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setUserId(userId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuNum(skuNum);
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setIsChecked(1);
            //异步
            cartInfoAsycService.insert(cartInfo1);
            redisTemplate.opsForHash().put(cacheKey,skuId.toString(),cartInfo1);
        }else {
            //当缓存有，添加数量
            Integer num = cartInfo.getSkuNum();
            cartInfo.setSkuNum(num+skuNum);
            //异步
            cartInfoAsycService.update(cartInfo);
            redisTemplate.opsForHash().put(cacheKey,skuId.toString(),cartInfo);
        }

    }

    @Override
    public CartInfo findCartInfo(Long skuId) {
        //获取userId或UserTempId 拦截器 FeignInterceptor
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId=AuthContextHolder.getUserTempId(request);
        }
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        if (redisTemplate.opsForHash().hasKey(cacheKey,skuId.toString())){
            return(CartInfo) redisTemplate.opsForHash().get(cacheKey,skuId.toString());
        }else {
            String userTempId = AuthContextHolder.getUserTempId(request);
            String cacheKeyTemp = RedisConst.USER_KEY_PREFIX+userTempId+RedisConst.USER_CART_KEY_SUFFIX;
            return (CartInfo) redisTemplate.opsForHash().get(cacheKeyTemp,skuId.toString());
        }


    }


    /**
     * 情况： 1.有userId ，没有userTempId   2.有userTempId 没有userId  3.有userId ，有userTempId
     * @param userId
     * @param userTempId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {
        if (StringUtils.isEmpty(userTempId)){
            //没有临时id
            return getCartListUtil(userId);
        }else {
            //有临时id
            if (StringUtils.isEmpty(userId)){
                //有临时id 没有userId
                return getCartListUtil(userTempId);
            }else {
                //有临时id 有userId
                return getMergeCartListUtil(userId,userTempId);
            }
        }
    }

    private List<CartInfo> getMergeCartListUtil(String userId, String userTempId) {
        //真实用户id对应的购物车集合
        List<CartInfo> cartInfoListByUserId = getCartListUtil(userId);
        //临时用户id对应的购物车集合
        List<CartInfo> cartInfoListByUserTempId = getCartListUtil(userTempId);

        if (!CollectionUtils.isEmpty(cartInfoListByUserId)){
            //真实用户id对应的购物车集合有数据
            if (!CollectionUtils.isEmpty(cartInfoListByUserTempId)){
                //临时用户id对应的购物车集合有数据 合并用户id对应的购物车集合、临时用户id对应的购物车集合
                //合并过程中 如果临时用户的集合数据中真实用户已经有了，则+商品数量 ，如果没有 则+商品
                Map<Long, CartInfo> cartInfoListByUserIdMap = cartInfoListByUserId.stream()
                        .collect(Collectors.toMap(CartInfo::getSkuId,CartInfo->CartInfo));
                for (CartInfo cartInfo : cartInfoListByUserTempId) {
                    Long skuId = cartInfo.getSkuId();
                    if (cartInfoListByUserIdMap.containsKey(skuId)){
                        //临时用户的集合数据中真实用户已经有了，则+商品数量
                        CartInfo info = cartInfoListByUserIdMap.get(skuId);
                        info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());
                        info.setIsChecked(1);
                        cartInfoListByUserIdMap.put(skuId,info);

                        //然后把临时用户id的redis数据以及mysql数据进行操作
                        //更新并删除DB
                        cartInfoMapper.update(info,new QueryWrapper<CartInfo>()
                                .eq("user_id",info.getUserId())
                                .eq("sku_id",info.getSkuId()));
                        cartInfoMapper.delete(new QueryWrapper<CartInfo>()
                                .eq("user_id",cartInfo.getUserId())
                                .eq("sku_id",cartInfo.getSkuId()));
                        //代码运行这个 userId肯定真实用户id
                        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
                        redisTemplate.opsForHash().put(cacheKey,skuId.toString(),info);


                    }else {
                        //如果没有 则+商品
                        cartInfoListByUserIdMap.put(cartInfo.getSkuId(),cartInfo);

                        //然后把临时用户id的redis数据以及mysql数据进行操作
                        cartInfo.setUserId(userId);
                        cartInfoMapper.update(cartInfo,new QueryWrapper<CartInfo>().eq("user_id",userTempId)
                        .eq("sku_id",cartInfo.getSkuId()));
                        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
                        redisTemplate.opsForHash().put(cacheKey,skuId.toString(),cartInfo);

                    }
                    //删除redis中临时用户id数据
                    String cacheKeyTemp = RedisConst.USER_KEY_PREFIX+userTempId+RedisConst.USER_CART_KEY_SUFFIX;
                    redisTemplate.delete(cacheKeyTemp);
                }
                //将Map转换List进行返回
                Collection<CartInfo> values = cartInfoListByUserIdMap.values();
                return new ArrayList<CartInfo>(values);

            }else {
                //临时用户id对应的购物车集合没有数据
                return cartInfoListByUserId;
            }
        }else {
            //真实用户id对应的购物车集合没有数据
            //更新db 将临时购物车对应的userid转换成真实的用户id
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfoMapper.update(cartInfo,new QueryWrapper<CartInfo>()
                    .eq("user_id",userTempId));

            //更新缓存
            Map<String, CartInfo> cartInfoMap = cartInfoListByUserTempId.stream().peek((cartInfo1) -> {
                cartInfo1.setUserId(userId);
            }).collect(Collectors.toMap(cartInfo1->{
               return cartInfo1.getSkuId().toString();
            },cartInfo1->cartInfo1));

            String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
            String cacheKeyTemp = RedisConst.USER_KEY_PREFIX+userTempId+RedisConst.USER_CART_KEY_SUFFIX;
            redisTemplate.opsForHash().putAll(cacheKey,cartInfoMap);
            redisTemplate.delete(cacheKeyTemp);
            return cartInfoListByUserTempId;
        }

    }

    private List<CartInfo> getCartListUtil(String userId) {
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        List<CartInfo> values = redisTemplate.opsForHash().values(cacheKey);
        //getCartList 需要实时价格而不是当时存入购物车价格
        for (CartInfo value : values) {
            BigDecimal price = productFeignClient.getPrice(value.getSkuId());
            value.setSkuPrice(price);
        }
        return values;
    }


    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        //获取userId或UserTempId 拦截器 FeignInterceptor
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId=AuthContextHolder.getUserTempId(request);
        }

        //更新数据库
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(isChecked);
        cartInfoMapper.update(cartInfo,new QueryWrapper<CartInfo>().eq("user_id",userId).eq("sku_id",skuId));
        //更新redis
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        CartInfo cartInfo1 = (CartInfo) redisTemplate.opsForHash().get(cacheKey, skuId.toString());
        cartInfo1.setIsChecked(isChecked);
        redisTemplate.opsForHash().put(cacheKey,skuId.toString(),cartInfo1);
    }

    //查询redis中isCheck属性为1的
    @Override
    public List<CartInfo> getCartInfoToTrade(String userId) {
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cacheKey);
        List<CartInfo> cartInfoIsChecked = cartInfoList.stream().filter((cartInfo) -> {
            if (1 == cartInfo.getIsChecked()) {
                return true;
            }
            return false;
        }).peek((cartInfo) -> {
            BigDecimal price = productFeignClient.getPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(price);
        }).collect(Collectors.toList());
        System.out.println(cartInfoIsChecked);
        return cartInfoIsChecked;

    }
}
