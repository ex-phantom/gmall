package com.shadow.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsSearchParam;
import com.shadow.gmall.beans.PmsSearchSkuInfo;
import com.shadow.gmall.service.PmsSearchSkuInfoServer;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;

import org.elasticsearch.search.builder.SearchSourceBuilder;


import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PmsSearchSkuInfoServerImpl implements PmsSearchSkuInfoServer {

    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> getSkuInfo(PmsSearchParam pmsSearchParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList=null;
        try {
            pmsSearchSkuInfoList =new ArrayList<>();
            //执行es的查询
            SearchResult searchResult = jestClient.execute(this.getSearch(pmsSearchParam));
            if(searchResult!=null){
                //获取结果
                List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
                for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                    PmsSearchSkuInfo pmsSearchSkuInfoExampl= hit.source;
                    //将高亮显示的skuName替换原本的skuName
                    Map<String, List<String>> highlight = hit.highlight;
                    if(highlight!=null&&!highlight.isEmpty()){
                        List<String> skuName = highlight.get("skuName");
                        pmsSearchSkuInfoExampl.setSkuName(skuName.get(0));
                    }
                    //放入数组中返回
                    pmsSearchSkuInfoList.add(pmsSearchSkuInfoExampl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pmsSearchSkuInfoList;
    }
    //生成查询的对象
    private Search getSearch(PmsSearchParam pmsSearchParam){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义一次查询返回数据的量,用于翻页的数据
        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //关键字查询
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            //匹配
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);

            //高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.postTags("<span style='color:red;font-weight:bold'>");
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("</span>");
            searchSourceBuilder.highlight(highlightBuilder);

        }
        //根据页面传输的此属性值id,查询所有符合的商品
        String[] valueIds = pmsSearchParam.getValueId();
        if(valueIds!=null&&valueIds.length>0){
            for (String valueId : valueIds) {
                //过滤
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //根据页面传输的三级分类的id,查询所有符合的商品
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if(StringUtils.isNotBlank(catalog3Id)){
            //过滤
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);
        System.out.println(searchSourceBuilder.toString());

        Search search=new Search.Builder(searchSourceBuilder.toString()).addIndex("gmall").addType("PmsSearchSkuInfo").build();

        return search;

    }



}
