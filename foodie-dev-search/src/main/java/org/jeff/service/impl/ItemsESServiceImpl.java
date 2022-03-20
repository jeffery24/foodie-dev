package org.jeff.service.impl;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jeff.pojo.Items;
import org.jeff.service.ItemsESService;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jeff
 * @since 1.0.0
 */
@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        String itemNameFiled = "itemName";

        Pageable pageable = PageRequest.of(page, pageSize);


        SortBuilder sortBuilder = null;
        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts")
                    .order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price")
                    .order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword")
                    .order(SortOrder.ASC);
        }

        //根据一个值查询多个字段  并高亮显示  这里的查询是取并集，即多个字段只需要有一个字段满足即可
        //需要查询的字段
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery(itemNameFiled, keywords))
                //.should(QueryBuilders.matchQuery("name",keywords))
                ;
        //构建高亮查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightFields(
                        new HighlightBuilder.Field(itemNameFiled)
                        //,new HighlightBuilder.Field("name")
                )
                .withHighlightBuilder(new HighlightBuilder()
                        // 让前端自定义样式 em
                        //.preTags(preTag).postTags(postTag)
                )
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();
        //分页
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        //(pageRequest);
        //查询
        org.springframework.data.elasticsearch.core.SearchHits<Items> search = esTemplate.search(searchQuery, Items.class);

        //得到查询返回的内容
        List<SearchHit<Items>> searchHits = search.getSearchHits();
        //设置一个最后需要返回的实体类集合
        List<Items> items = new ArrayList<>();
        //遍历返回的内容进行处理
        for (SearchHit<Items> searchHit : searchHits) {
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setItemName(highlightFields.get(itemNameFiled) == null ? searchHit.getContent().getItemName() : highlightFields.get(itemNameFiled).get(0));
            //searchHit.getContent().set(highlightFields.get("info")==null ? searchHit.getContent().getInfo():highlightFields.get("info").get(0));
            //放到实体类中
            items.add(searchHit.getContent());
        }

        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(items);
        gridResult.setPage(page + 1);
        gridResult.setTotal(search.getTotalHits() % pageSize == 0 ? Math.toIntExact(search.getTotalHits() / pageSize) : Math.toIntExact(search.getTotalHits() / pageSize) + 1);
        gridResult.setRecords((int) search.getTotalHits());
        return gridResult;
    }
}
