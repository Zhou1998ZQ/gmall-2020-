package com.pdmxz.gmall.list.service;

import com.pdmxz.gmall.model.list.SearchParam;
import com.pdmxz.gmall.model.list.SearchResponseVo;

public interface ListService {
    void onSale(Long skuId);

    void onCancel(Long skuId);

    void incrHotScore(Long skuId, Integer score);

    SearchResponseVo searchResponseVo(SearchParam searchParam);
}
