package com.mr.pojo;

/**
 * @ClassName Item
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/4
 * @Version V1.0
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

        /*
        indexName:用于存储此实体的索引的名称
        type:映射类型;（从版本4.0开始不推荐使用）
        shards:索引的分片数
        replicas:索引的副本数
        */
@Document(indexName = "item",type = "docs", shards = 1, replicas = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id  //作用在成员变量，标记一个字段作为id主键
    private Long id;
    //作用在成员变量，标记为文档的字段，并指定字段映射属性
    //type：字段类型，是枚举：FieldType
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; //标题

    @Field(type = FieldType.Keyword)
    private String category;// 分类

    @Field(type = FieldType.Keyword)
    private String brand; // 品牌

    @Field(type = FieldType.Double)
    private Double price; // 价格

    @Field(index = false, type = FieldType.Keyword)
    private String images; // 图片地址
}