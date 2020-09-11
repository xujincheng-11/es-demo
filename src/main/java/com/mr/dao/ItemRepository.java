package com.mr.dao;

import com.mr.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item,Long> {
    //自定义条件查询方法(query和find一样的效果)
    //品牌查询
    public List<Item> queryByBrand(String brand);

    //根据价格区间查询
    public List<Item> queryByPriceBetween(double p1,double p2);

    //品牌和价格区间同时查询
    public List<Item> findByBrandAndPriceBetween(String brand,double p1,double p2);
}
