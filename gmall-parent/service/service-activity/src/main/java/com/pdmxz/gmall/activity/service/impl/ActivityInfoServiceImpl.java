package com.pdmxz.gmall.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.activity.mapper.SeckillGoodsMapper;
import com.pdmxz.gmall.activity.service.ActivityInfoService;
import com.pdmxz.gmall.activity.utils.DateUtil;
import com.pdmxz.gmall.common.constant.RedisConst;
import com.pdmxz.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ActivityInfoServiceImpl implements ActivityInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Override
    public void sendActivityInfoToRedis() {
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status","1");
        queryWrapper.gt("stock_count",0);
        String date = DateUtil.formatDate(new Date());
        queryWrapper.eq("DATE_FORMAT(start_time,'%Y-%m-%d')",date);
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(queryWrapper);
        //将SeckillGoods存入缓存
        for (SeckillGoods seckillGood : seckillGoods) {
            redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS,seckillGood.getSkuId().toString(),seckillGood);
            Integer stockCount = seckillGood.getStockCount();
            String skuId = seckillGood.getSkuId().toString();
            String[] skuids = new String[stockCount];
            for (int i = 0; i < skuids.length; i++) {
                skuids[i] = skuId;
            }
            //将SeckillGoods存入缓存
            redisTemplate.opsForList().leftPushAll(RedisConst.SECKILL_STOCK_PREFIX+skuId,skuids);
        }

    }

    @Override
    public List<SeckillGoods> getAllSeckillGoods() {
        return redisTemplate.opsForHash().values(RedisConst.SECKILL_GOODS);
    }

    @Override
    public SeckillGoods getSeckillGoodsBySkuId(Long skuId) {
       return (SeckillGoods) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS,skuId.toString());
    }
}
