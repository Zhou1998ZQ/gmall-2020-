package com.pdmxz.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.list.dao.GoodsDao;
import com.pdmxz.gmall.list.service.ListService;
import com.pdmxz.gmall.model.list.*;
import com.pdmxz.gmall.model.product.BaseCategoryView;
import com.pdmxz.gmall.model.product.BaseTrademark;
import com.pdmxz.gmall.model.product.SkuAttrValue;
import com.pdmxz.gmall.model.product.SkuInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {


    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        //通过sku表获取信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        Long id = skuInfo.getId();
        String skuName = skuInfo.getSkuName();
        String skuDefaultImg = skuInfo.getSkuDefaultImg();
        BigDecimal price = skuInfo.getPrice();
        goods.setId(id);
        goods.setTitle(skuName);
        goods.setDefaultImg(skuDefaultImg);
        goods.setPrice(price.doubleValue());

        //新品上架时间
        Calendar calendar = Calendar.getInstance();
        goods.setCreateTime(calendar.getTime());

        //通过base_trademark表获取信息
        Long tmId = skuInfo.getTmId();
        BaseTrademark baseTrademark = productFeignClient.getBaseTrademark(tmId);
        Long trademarkId = baseTrademark.getId();
        String tmName = baseTrademark.getTmName();
        String logoUrl = baseTrademark.getLogoUrl();
        goods.setTmId(trademarkId);
        goods.setTmName(tmName);
        goods.setTmLogoUrl(logoUrl);

        //通过base_category视图获取信息
        Long category3Id1 = skuInfo.getCategory3Id();
        BaseCategoryView baseCategoryView = productFeignClient.getCategory(category3Id1);
        Long category1Id = baseCategoryView.getCategory1Id();
        Long category2Id = baseCategoryView.getCategory2Id();
        Long category3Id = baseCategoryView.getCategory3Id();
        String category1Name = baseCategoryView.getCategory1Name();
        String category2Name = baseCategoryView.getCategory2Name();
        String category3Name = baseCategoryView.getCategory3Name();
        goods.setCategory1Id(category1Id);
        goods.setCategory2Id(category2Id);
        goods.setCategory3Id(category3Id);
        goods.setCategory1Name(category1Name);
        goods.setCategory2Name(category2Name);
        goods.setCategory3Name(category3Name);

        List<SkuAttrValue> skuAttrValueList = productFeignClient.getAttrList(skuId);
        List<SearchAttr> searchAttrList = skuAttrValueList.stream().map((skuAttrValue) -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(skuAttrValue.getBaseAttrInfo().getId());
            searchAttr.setAttrName(skuAttrValue.getBaseAttrInfo().getAttrName());
            searchAttr.setAttrValue(skuAttrValue.getBaseAttrValue().getValueName());

            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchAttrList);

        goodsDao.save(goods);
    }

    /**
     * 下架
     *
     * @param skuId
     */
    @Override
    public void onCancel(Long skuId) {
        goodsDao.deleteById(skuId);
    }

    /**
     * 给skuId对应的商品增加热点分数
     *
     * @param skuId
     * @param score
     */
    @Override
    public void incrHotScore(Long skuId, Integer score) {
        String HotScore = "HotScore";
        Double incrementScore = redisTemplate.opsForZSet().incrementScore(HotScore, skuId, score);
        //因为es库的修改操作慢，为了避免平反修改数据 incrementScore为10的倍数时修改HotScore
        if (incrementScore % 10 == 0) {
            Optional<Goods> goodsOptional = goodsDao.findById(skuId);
            Goods goods = goodsOptional.get();
            goods.setHotScore(incrementScore.longValue());
            goodsDao.save(goods);
        }
    }

    /**
     * 搜索
     *
     * @param searchParam
     * @return
     */
    @Override
    public SearchResponseVo searchResponseVo(SearchParam searchParam) {
        //搜索请求对象

        SearchRequest searchRequest = buildSearchRequest(searchParam);
        //开始搜索

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchResponseVo searchResponseVo = buildSearchResponse(searchResponse);

            searchResponseVo.setPageNo(searchParam.getPageNo());
            searchResponseVo.setPageSize(searchParam.getPageSize());
            searchResponseVo.setTotalPages((searchResponseVo.getTotal()+searchResponseVo.getPageSize()-1)/searchResponseVo.getPageSize());
            return searchResponseVo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建搜索请求对象
     * <p>
     * GET goods/_search
     * {
     * "query": {
     * "bool": {
     * "must": [
     * {"match": {
     * "title": "手机"
     * }       }
     * ]
     * }
     * },
     * "from": 0,
     * "size": 20
     * }
     *
     * @param searchParam
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {

        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //组合对象


        //查询关键词 must  matchQuery ->会进行分词查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword));
        }
        //查询品牌 filter termQuery ->不会进行分词查询
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = trademark.split(":");
            boolQueryBuilder.filter(QueryBuilders.termQuery("tmId", split[0]));
        }

        //查询平台属性 props=23:4G:运行内存
        String[] props = searchParam.getProps();

        if (props != null && props.length > 0) {
            //用于接收子对象
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (String prop : props) {
                String[] split = prop.split(":");
                if (split != null && split.length == 3) {
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                    boolQuery.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                    queryBuilder.must(QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None));
                }
            }

            boolQueryBuilder.filter(queryBuilder);
        }


        //查询三级分类 filter termQuery ->不会进行分词查询
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();
        if (category1Id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1Id", category1Id));
        }
        if (category2Id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2Id", category2Id));
        }
        if (category3Id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category3Id", category3Id));
        }

        //排序 order=1:asc
        String order = searchParam.getOrder();
        String[] split = order.split(":");
        if (split != null && split.length == 2) {
            String s = split[0];
            String orderValue = "";
            switch (s) {
                case "1":
                    orderValue = "HotScore";
                    break;
                case "2":
                    orderValue = "price";
                    break;
                case "3":
                    orderValue = "createTime";
                    break;
            }
            searchSourceBuilder.sort(orderValue, ("asc").equalsIgnoreCase(split[1]) ? SortOrder.ASC : SortOrder.DESC);
        }

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("title");
        searchSourceBuilder.highlighter(highlightBuilder);
        //设置分页
        searchSourceBuilder.from((searchParam.getPageNo() - 1) * searchParam.getPageSize());
        searchSourceBuilder.size(searchParam.getPageSize());

        //品牌桶聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("tmIdAgg").field("tmId")
        .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
        .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl")
        ));
        // 平台属性桶聚合
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrsAgg","attrs")
        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")))
        );

        searchSourceBuilder.query(boolQueryBuilder);
        System.out.println(searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }


    public SearchResponseVo buildSearchResponse(SearchResponse searchResponse) {



        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Goods> list = Arrays.stream(hits).map((hit) -> {
            String sourceAsString = hit.getSourceAsString();
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //解析高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            String s = title.fragments()[0].toString();
            goods.setTitle(s);

            return goods;
        }).collect(Collectors.toList());
        //解析分页
        long total = searchResponse.getHits().getTotalHits();
        searchResponseVo.setTotal(total);

        //解析品牌桶聚合
        ParsedLongTerms tmIdAgg = searchResponse.getAggregations().get("tmIdAgg");
        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();
        List<SearchResponseTmVo> searchResponseTmVos = buckets.stream().map(
                (bucket)->{
                    SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
                    //品牌Id
                    long longValue = bucket.getKeyAsNumber().longValue();
                    searchResponseTmVo.setTmId(longValue);
                    //品牌名称
                    ParsedStringTerms tmNameAggTerms = bucket.getAggregations().get("tmNameAgg");
                    String tmNameAgg = tmNameAggTerms.getBuckets().get(0).getKeyAsString();
                    searchResponseTmVo.setTmName(tmNameAgg);
                    //品牌logoUrl
                    ParsedStringTerms tmLogoUrlAggTerms = bucket.getAggregations().get("tmLogoUrlAgg");
                    String tmLogoUrlAgg = tmLogoUrlAggTerms.getBuckets().get(0).getKeyAsString();
                    searchResponseTmVo.setTmLogoUrl(tmLogoUrlAgg);
                    return searchResponseTmVo;
                }
        ).collect(Collectors.toList());

        //解析平台属性集合
        ParsedNested attrsAgg = searchResponse.getAggregations().get("attrsAgg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> buckets1 = attrIdAgg.getBuckets();
        List<SearchResponseAttrVo> searchResponseAttrVos = buckets1.stream().map((bucket)->{
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //平台属性id
            long longValue = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(longValue);
            //平台属性名称
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String keyAsString = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(keyAsString);
            //平台属性值集合
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<? extends Terms.Bucket> buckets2 = attrValueAgg.getBuckets();
            List<String> attrValueLists = buckets2.stream().map((bucket1)->{
                return bucket1.getKeyAsString();
            }).collect(Collectors.toList());

            searchResponseAttrVo.setAttrValueList(attrValueLists);
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        searchResponseVo.setTrademarkList(searchResponseTmVos);
        searchResponseVo.setGoodsList(list);
        searchResponseVo.setAttrsList(searchResponseAttrVos);
        return searchResponseVo;
    }
}
