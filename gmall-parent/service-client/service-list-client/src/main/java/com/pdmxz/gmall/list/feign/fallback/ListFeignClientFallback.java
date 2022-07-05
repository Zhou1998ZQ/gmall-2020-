package com.pdmxz.gmall.list.feign.fallback;

import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.list.feign.ListFeignClient;
import com.pdmxz.gmall.model.list.SearchParam;
import com.pdmxz.gmall.model.list.SearchResponseVo;
import org.springframework.stereotype.Component;

@Component
public class ListFeignClientFallback implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId, Integer score) {
        return null;
    }

    @Override
    public SearchResponseVo searchResponseVo(SearchParam searchParam) {
        return null;
    }
}
