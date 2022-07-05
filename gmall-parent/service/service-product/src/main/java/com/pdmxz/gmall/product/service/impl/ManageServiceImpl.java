package com.pdmxz.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdmxz.gmall.common.cache.GmallCache;
import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.common.constant.RedisConst;
import com.pdmxz.gmall.common.service.RabbitService;
import com.pdmxz.gmall.model.product.*;
import com.pdmxz.gmall.product.mapper.*;
import com.pdmxz.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id", category1Id);
        return baseCategory2Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }


    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.attrInfoList(category1Id, category2Id, category3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() != null) {
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();

            attrValueList.forEach((attrValue) -> {
                baseAttrValueQueryWrapper.eq("id", attrValue.getId());
                baseAttrValueMapper.update(attrValue, baseAttrValueQueryWrapper);
            });
        } else {
            baseAttrInfoMapper.insert(baseAttrInfo);
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            attrValueList.forEach((attrValue) -> {
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(attrValue);
            });
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper();
        queryWrapper.eq("attr_id", attrId);
        return baseAttrValueMapper.selectList(queryWrapper);

    }

    @Override
    public IPage baseTrademark(Integer page, Integer limit) {
        return baseTrademarkMapper.selectPage(new Page<>(page, limit), null);
    }

    @Override
    public void save(BaseTrademark baseTrademark) {
        baseTrademarkMapper.insert(baseTrademark);
    }

    @Override
    public void remove(Long id) {
        baseTrademarkMapper.deleteById(id);
    }

    @Override
    public BaseTrademark get(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    @Override
    public void update(BaseTrademark baseTrademark) {
        baseTrademarkMapper.updateById(baseTrademark);
    }

    @Override
    public IPage page(Integer page, Integer limit, Long category3Id) {
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        return spuInfoMapper.selectPage(spuInfoPage, queryWrapper);

    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        spuImageList.forEach((spuImage) -> {
            spuImage.setSpuId(spuInfo.getId());
            spuImageMapper.insert(spuImage);
        });
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrList.forEach((spuSaleAttr) -> {
            spuSaleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insert(spuSaleAttr);
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            spuSaleAttrValueList.forEach((spuSaleAttrValue) -> {
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            });
        });
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
    }


    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.spuSaleAttrList(spuId);
    }


    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        skuAttrValueList.forEach((skuAttrValue) -> {
            skuAttrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insert(skuAttrValue);
        });
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        skuSaleAttrValueList.forEach((skuSaleAttrValue) -> {
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        });
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        skuImageList.forEach((skuImage) -> {
            skuImage.setSkuId(skuInfo.getId());
            skuImageMapper.insert(skuImage);
        });

    }

    @Override
    public IPage<SkuInfo> list(Integer page, Integer limit) {
        return skuInfoMapper.selectPage(new Page<SkuInfo>(page, limit), null);
    }

    @Autowired
    private RabbitService rabbitService;

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(SkuInfo.ISSALE);
        skuInfoMapper.updateById(skuInfo);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS,MqConst.ROUTING_GOODS_UPPER,skuId);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(SkuInfo.NOSALE);
        skuInfoMapper.updateById(skuInfo);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS,MqConst.ROUTING_GOODS_LOWER,skuId);
    }


    //根据skuId查询库存信息和图片集合
    @Override
    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX)
    public SkuInfo getSkuInfo(Long skuId) {

            SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
            List<SkuImage> skuImageList = skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id", skuId));
            if (skuInfo!=null){
                skuInfo.setSkuImageList(skuImageList);
            }

            return skuInfo;

    }

    //查询3级分类 对应数据库得视图BaseCategoryView
    @Override
    @GmallCache(prefix = RedisConst.Category_PREFIX)
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    //单独查询价格，因为不放进缓存中
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo != null) {
            return skuInfo.getPrice();
        }
        return null;
    }

    //**难**  通过skuId，spuId获取商品对应的属性和属性值以及被选中的库存
    @Override
    @GmallCache(prefix = RedisConst.SpuSaleAttrList_PREFIX)
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    //**难**  通过spuId 获取所有商品对应的销售属性值的集合
    @Override
    @GmallCache(prefix = RedisConst.SkuValueIdsMap_PREFIX)
    public Map getSkuValueIdsMap(Long spuId) {
        List<Map> mapLists = skuSaleAttrValueMapper.getSkuValueIdsMap(spuId);
        Map map = new HashMap<>();
        mapLists.forEach((mapList) -> {
            map.put(mapList.get("values_id"), mapList.get("sku_id"));
        });

        return map;


    }


    @Override
    public List<BaseCategoryView> getIndex() {
      return   baseCategoryViewMapper.selectList(null);
    }


    @Override
    public BaseTrademark getBaseTrademark(Long tmId) {
       return baseTrademarkMapper.selectById(tmId);

    }

    @Override
    public List<SkuAttrValue> getAttrList(Long skuId) {
        return   skuAttrValueMapper.getAttrList(skuId);
    }
}
