package test.es.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.es.exceptions.ClientException;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: nobody
 * @Date: 2020-10-05 13:42
 * @Desc:
 */
@Slf4j
@Service
public class EsService {

    @Autowired
    private RestHighLevelClient highLevelClient;


    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    public Object createIndex(String index) {
        //检查索引是否存在
        ClientException.checkForThrow(checkIndexExistence(index), "索引已存在，无需重复创建！");
        //创建索引对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        //客户端执行请求
        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = highLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createIndexResponse;
    }

    /**
     * 复制源索引的文档至目标索引
     *
     * @param sourceIndices 源索引
     * @param destIndex     目标索引
     * @return
     */
    public Object reindex(String destIndex, String... sourceIndices) {
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(sourceIndices).setDestIndex(destIndex)
                .setDestVersionType(VersionType.EXTERNAL)//保存原索引文档的版本，现索引中存在的文档将被修改，不存在的将新建
                .setDestOpType(DocWriteRequest.OpType.CREATE.getLowercase())//在目标索引中创建缺失文档，所有现有文档将导致版本冲突
                .setAbortOnVersionConflict(false);
        try {
            return highLevelClient.reindex(reindexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查索引是否存在
     *
     * @param index
     * @return
     */
    public boolean checkIndexExistence(String index) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        boolean exists = false;
        try {
            exists = highLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public AcknowledgedResponse deleteIndex(String index) {
        //检查索引是否存在
        ClientException.checkForThrow(!checkIndexExistence(index), "索引不存在！");
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = null;
        try {
            delete = highLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delete;
    }

    /**
     * 创建文档
     *
     * @param index
     * @param content 文档内容
     * @return
     */
    public IndexResponse createDoc(String index, Map<String, ?> content) {
        return createDoc(index, IdUtil.getSnowflake(1, 1).nextIdStr(), content);
    }

    public IndexResponse createDoc(String index, String id, Map<String, ?> content) {
        //创建请求
        IndexRequest request = new IndexRequest(index);
        request.id(id)
                .timeout(TimeValue.timeValueSeconds(3))
                .source(content)
                .opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = null;
        try {
            //发送请求
            indexResponse = highLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexResponse;
    }

    /**
     * 查找文档
     *
     * @param index         索引
     * @param id            文档id
     * @param includeFields 需要查询的字段 不传为查全部
     * @return
     */
    public Object getDoc(String index, String id, String[] includeFields) {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = null;
        try {
            if (includeFields != null && includeFields.length != 0) {
                getRequest.fetchSourceContext(new FetchSourceContext(true, includeFields, Strings.EMPTY_ARRAY));
            }
            getResponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponse;
    }

    /**
     * 检查文档是否存在
     *
     * @param index
     * @param docId
     * @return
     */
    public boolean checkDocExistence(String index, String docId) {
        GetRequest getRequest = new GetRequest(index, docId)
                .fetchSourceContext(new FetchSourceContext(false))
                .storedFields("_none_");
        try {
            return highLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文档
     *
     * @param index
     * @param docId
     * @return
     */
    public RestStatus deleteDoc(String index, String docId) {
        DeleteRequest deleteRequest = new DeleteRequest(index, docId);
        try {
            return highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT).status();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改文档
     *
     * @param index
     * @param id
     * @param params
     * @return
     */
    public Object updateDoc(String index, String id, Object params) {
        UpdateRequest updateRequest = new UpdateRequest(index, id)
                .doc(params, XContentType.JSON)
                .docAsUpsert(true)//不存在则新建
                .retryOnConflict(5);//修改遇到冲突时重试次数

        //或者使用脚本更新
        /*Script inline = new Script(ScriptType.INLINE, "painless",
                "ctx._source.field += params.count", params);*/
        //使用已保存的脚本
        /*Script stored = new Script(
                ScriptType.STORED, null, "increment-field", params)*/
        ;
        try {
            return highLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据查询条件修改
     *
     * @param params
     * @param indexes
     * @return
     */
    public Object updateByQuery(Map params, String... indexes) {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(indexes)
                .setQuery(new TermQueryBuilder("name", "abc"))
                .setScript(new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG,
                        "ctx._source.age=params.age", params))
                .setAbortOnVersionConflict(false);
        try {
            return highLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据查询条件删除
     *
     * @param name
     * @param sourceIndices
     * @return
     */
    public Object deleteByQuery(String name, String[] sourceIndices) {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(sourceIndices)
                .setQuery(new TermQueryBuilder("name", name))
                .setAbortOnVersionConflict(false);
        try {
            return highLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 全量搜索
     *
     * @param indexes
     * @return
     */
    public Object search(String... indexes) {
        SearchRequest searchRequest;
        if (indexes != null && indexes.length > 0) {
            searchRequest = new SearchRequest(indexes);
        } else {
            searchRequest = new SearchRequest();
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                .sort(new ScoreSortBuilder().order(SortOrder.DESC))//分数降序排序
                .sort(new FieldSortBuilder("id").order(SortOrder.ASC));//id升序排序
        searchRequest.source(searchSourceBuilder);
        try {
            return highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量查询文档
     *
     * @param index
     * @param ids
     * @return
     */
    public Object multiGetDocs(String index, String[] ids) {
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        for (String id : ids) {
            multiGetRequest.add(index, id);
        }
        try {
            return highLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 条件搜索
     *
     * @param field   全文检索的字段
     * @param value   字段值
     * @param indexes
     * @return
     */
    public SearchResponse searchByConditions(String field, String value, String... indexes) {
        SearchRequest searchRequest = new SearchRequest(indexes);
        HighlightBuilder.Field field1 = new HighlightBuilder.Field(field).highlighterType("unified");
        HighlightBuilder highlightBuilder = new HighlightBuilder().field(field1);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(field, value))
//        searchSourceBuilder.query(QueryBuilders.termQuery(field, value)) //term搜索
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery()) //搜索所有
//        searchSourceBuilder.query(QueryBuilders.prefixQuery(field, value)) //按前缀搜索
//        searchSourceBuilder.query(QueryBuilders.boolQuery()
//        .should(QueryBuilders.matchQuery(field,value))) //bool搜索should
//        searchSourceBuilder.query(QueryBuilders.boolQuery()
//        .must(QueryBuilders.matchQuery(field,value))) //bool搜索must
//        searchSourceBuilder.query(QueryBuilders.boolQuery()
//        .mustNot(QueryBuilders.matchQuery(field,value))) //bool搜索mustNot
                .highlighter(highlightBuilder);//高亮展示

        searchRequest.source(searchSourceBuilder)
                .preference("_local");//使用当前分片处理搜索请求，默认为随机一个分片来处理
        SearchResponse searchResponse = null;
        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

    /**
     * 聚合
     *
     * @param index
     * @return
     */
    public Avg searchAggrs(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(SearchSourceBuilder
                .searchSource()
                .aggregation(AggregationBuilders
                        .avg("avg_age")
                        .field("age")));
        try {
            SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Avg averageAge = searchResponse.getAggregations().get("avg_age");
            return averageAge;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索建议
     *
     * @param index
     * @param field
     * @param text
     * @return
     */
    public Suggest searchSuggestions(String index, String field, String text) {
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        CompletionSuggestionBuilder text1 = SuggestBuilders.completionSuggestion(field).text(text);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("completion", text1);
        searchSourceBuilder.suggest(suggestBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse search = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return search.getSuggest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步批量处理数据
     * @param bulkRequest
     * @return
     */
    public Cancellable asyncBulkOp(BulkRequest bulkRequest) {
        return highLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                log.info("批量处理数据完毕", bulkItemResponses);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                log.error("处理数据异常，", e.getMessage());
            }
        });

    }
}
