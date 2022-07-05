package com.pdmxz.gmall.list.dao;

import com.pdmxz.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {
}
