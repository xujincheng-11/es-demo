package EsTest;

import com.mr.EsApplication;
import com.mr.dao.HignLightUtil;
import com.mr.dao.ItemRepository;
import com.mr.pojo.Item;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName EsTest
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/4
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EsApplication.class})
public class EsTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Test
    public void testCreateIndex(){
        //创建索引
        elasticsearchTemplate.createIndex(Item.class);
        System.out.println("创建索引");
        //要想有字段,需创建映射(mapping)
        elasticsearchTemplate.putMapping(Item.class);
        System.out.println("创建映射");
    }

    @Test
    public void  testDeleteIndex(){
        //删除索引
        elasticsearchTemplate.deleteIndex(Item.class);
        System.out.println("删除索引");
    }

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testSaveItem(){
        //创建item实体类变量/新增
        Item item = new Item(9527l,"华为冰岛幻境","手机","华为",2300d,"huawei.png");
        //保存9527l,"华为冰岛幻境","手机","华为",2300d,"huawei.png"
        itemRepository.save(item);
        System.out.println("新增成功");
    }
    @Test
    public void saveItemList(){
        //批量新增
        //定义多个Item加入集合
        Item item1=new Item(9528l,"华为手机冰岛幻境","手机","华为",2300.0,"huawei.png");
        Item item2=new Item(9529l,"华为蓝水翡翠","手机","华为",2400.0,"huawei.png");
        Item item3=new Item(9530l,"小米手机","手机","小米",1300.0,"xiaomi.png");
        Item item4=new Item(9531l,"小米手机超值版","手机","小米",1800.0,"xiaomi.png");

        List<Item> list= Arrays.asList(item1,item2,item3,item4);
        //批量保存多条数据
        itemRepository.saveAll(list);
        System.out.println("批量新增成功");
    }
    @Test
    public void  updateItem(){
        //修改
        Item item=new Item(9529l,"华为蓝水翡翠pro","手机","华为",2600.0,"huawei.png");
        //save既是新增也是修改
        itemRepository.save(item);
        System.out.println("修改成功");
    }
    @Test
    public void updateItemList(){
        //批量修改
        //定义多个Item加入集合
        Item item1=new Item(9528l,"华为冰岛幻境pro","手机","华为",2300.0,"huawei.png");
        Item item2=new Item(9529l,"华为蓝水翡翠pro","手机","华为",2700.0,"huawei.png");
        Item item3=new Item(9530l,"小米手机10","手机","小米",1300.0,"xiaomi.png");
        Item item4=new Item(9531l,"小米手机青春版","手机","小米",1800.0,"xiaomi.png");

        List<Item> list= Arrays.asList(item1,item2,item3,item4);
        //批量保存多条数据
        itemRepository.saveAll(list);
        System.out.println("批量修改成功");
    }
    @Test
    public void  deleteItem(){
        //删除方法
        Item item=new Item();
        item.setId(9528l);
        //删除方法
        itemRepository.delete(item);
        System.out.println("删除成功");
    }
    @Test
    public void  deleteItemList(){
        //批量删除方法
        Item item=new Item();
        List<Long> list= Arrays.asList(9529l,9530l,9531l);
        list.forEach(id->{

            item.setId(id);
            itemRepository.delete(item);
        });
        //删除方法
        System.out.println("批量删除成功");
    }
    @Test
    public void findItem(){
        //查询所有
        Iterable<Item> list = itemRepository.findAll();
        list.forEach(item->{
            System.out.println(item.getTitle()+"  "+item.getBrand()+" "+item.getPrice());
        });
    }
    @Test
    public void findItemSort(){
        //查询所有,根据价格降序输出
        Iterable<Item> list = itemRepository.findAll(Sort.by("price").descending());
        list.forEach(item->{
            System.out.println(item.getTitle()+"  "+item.getBrand()+"  "+item.getPrice());
        });
    }
    @Test //简单条件查询(自定义方法查询)
    public void findItemByParam(){
        //条件查询(品牌=华为; )
        //Iterable<Item> list = itemRepository.queryByBrand("华为");

        //根据价格区间查询1500<价格<2500
        //Iterable<Item> list = itemRepository.queryByPriceBetween(1500,2500);

        //品牌和价格区间同时查询
        Iterable<Item> list = itemRepository.findByBrandAndPriceBetween("华为",2500,3000);
        list.forEach(item->{
            System.out.println(item.getTitle()+"  "+item.getBrand()+"  "+item.getPrice());
        });
    }

    //复杂条件查询
    // 如 高亮(选中显示颜色),聚合(max,min,avg),过滤filter,模糊查询(fuzzy)
    // 范围查询(range),布尔组合（bool),多词条精确匹配(terms)等等
    @Test
    public void searchParam(){
        /*SearchQuery:查询类,包含查询条件;
        QueryBuilders:构造各种查询条件match matchAll  range区间
    父类的引用指向子类对象,父类(SearchQuery)可以调用子类(NativeSearchQuery)的特性
    子类只有有参构造,所以要传参数QueryBuilder;子类是:接口,要用工具类调方法
      */
        //分页查询全部
        //SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchAllQuery());

        //分页,单个条件查询
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("title","华为"));

        Page<Item> page = itemRepository.search(searchQuery);

        System.out.println("总条数: "+page.getTotalElements()+"总页数: "+page.getTotalPages());

        page.forEach(item->{
            System.out.println(item.getTitle()+"  "+item.getBrand()+"  "+item.getPrice());
        });
    }
    //复杂条件查询
    // 如 高亮(选中显示颜色),( 聚合aggregations)聚合函数(max,min,avg),过滤filter,模糊查询(fuzzy)
    // 范围查询(range),布尔组合（bool),多词条精确匹配(terms)等等
    @Test
    public void searchParam1(){
        //多个条件查询
        //条件构建类,又叫建造者模式,
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //进行条件筛选; 条件失真,加.operator(Operator.AND),就是同时满足条件
        //builder.withQuery(QueryBuilders.matchQuery("title","华为"));

        //价格区间筛选
        //builder.withQuery(QueryBuilders.rangeQuery("price").gt(100).lt(2500));

        //builder.withQuery:设置多次是覆盖关系
        //多条件使用bool查询如下:must可以将多条件进行组合
        builder.withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("title","小米手机").operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("price").gt(100).lt(2500))
        );
        //增加排序
        builder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //设置分页,参数一第几页,参数二每页几条
        builder.withPageable(PageRequest.of(1,1));
        // 查询
        Page<Item> page = itemRepository.search(builder.build());

        System.out.println("总页数: "+page.getTotalPages()+"总条数: "+page.getTotalElements());

        //循环数据
        page.forEach(item->{
            System.out.println(item.getTitle()+"  "+item.getBrand()+"  "+item.getPrice());
        });
    }

    //高亮显示(选中显示颜色)
    @Test
    public void searchParamHignLight(){
        //多个条件查询
        //条件构建类,又叫建造者模式,
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //builder.withQuery:设置多次是覆盖关系
        //多条件使用bool查询如下:must可以将多条件进行组合
        builder.withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("title","小米手机").operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("price").gt(1500).lt(2500))
        );
        //设置高亮查询;//1.根据标题高亮;2.//设置高亮前缀;3.//设置高亮后缀
        builder.withHighlightFields(
                new HighlightBuilder.Field("title")
                .preTags("<font color='red'>")
                .postTags("</font>")
        );
        //高亮设置
        //参数1.Spring对ES的java api进行的封装,提供公共类; 参数2.查询条件;
        //参数3:查询那个索引库;参数4:高亮的字段
        //返回高亮数据的id和高亮数据的类型
        Map<Long,String> hignLightMap = HignLightUtil.getHignLigntMap(elasticsearchTemplate,builder.build(),Item.class,"title");

        //增加排序
        builder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //设置分页,参数一第几页,参数二每页几条
        builder.withPageable(PageRequest.of(0,10));
        // 查询
        Page<Item> page = itemRepository.search(builder.build());

        //System.out.println("高亮map:"+hignLightMap);
        System.out.println("总页数: "+page.getTotalPages()+"总条数: "+page.getTotalElements());

        //循环查询数据
        page.forEach(item->{
            //先获得循环后的id,通过高亮加工数据中title,又把高亮的title,
            // 赋值给循环后的item的title,最后前台显示高亮标题
            item.setTitle(hignLightMap.get(item.getId()));
            System.out.println(item.getTitle()+"  "+item.getBrand()+"  "+item.getPrice());
        });
    }

    //1.聚合为桶,桶就是分组( 聚合aggregations);2.嵌套聚合，聚合函数值(俗称度量:聚合函数(max,min,avg))
    @Test //1.按照品牌brand进行分组：2.度量:聚合函数(max,min,avg)
    public void searchGroup(){
        //条件构造器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //增加分组查询(根据需求定义度量--max--min--avg等) 根据词条分组terms  brand_gro:别名                        别名
        builder.addAggregation(AggregationBuilders.terms("brand_gro").field("brand")
                //根据增加度量查询最大价格  max_price:别名
                .subAggregation(AggregationBuilders.max("max_price").field("price"))
                .subAggregation(AggregationBuilders.min("min_price").field("price"))
        );
        //返回分组的分页,需强转; 根据别名分组查询
        AggregatedPage<Item> aggregatedPage = (AggregatedPage<Item>) itemRepository.search(builder.build());
        //根据别名获取分组字段中的数据
        StringTerms stringTerms = (StringTerms) aggregatedPage.getAggregation("brand_gro");
        //获得buckets 分组数据集(因为数据被封装在里面)
        List<StringTerms.Bucket> bucketList = stringTerms.getBuckets();

        //循环体
        bucketList.forEach(bucket -> {
            //因为值在分组中,需要在循环中获取度量最大数据 (聚合函数)
            InternalMax max= (InternalMax)bucket.getAggregations().asMap().get("max_price");
            //获取度量最小数据 (聚合函数)
            InternalMin min= (InternalMin)bucket.getAggregations().asMap().get("min_price");
            //获得聚合后key 和 key下的文档数量
            System.out.println("key:"+bucket.getKey()+";  文档数量:"+bucket.getDocCount()+
                    ";  最大度量价格:"+max.getValue()+"; 最小度量价格:"+min.getValue());
        });
    }
}
