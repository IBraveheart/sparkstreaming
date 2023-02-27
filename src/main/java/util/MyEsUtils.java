package util;

import bean.DauInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Akang
 * @create 2022-12-02 10:59
 */
public class MyEsUtils {


    /**
     * 关闭es对象
     */
    public static void closeEsClient(RestHighLevelClient esClient) throws IOException {
        if (esClient != null) {
            esClient.close();
        }
    }

    /**
     * 获得 es连接
     */
    public static RestHighLevelClient getEsClient(String host, Integer port) {
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port));
        RestHighLevelClient esClient = new RestHighLevelClient(restClientBuilder);
        return esClient;
    }

    /**
     * 批写
     * 幂等写
     */

    public static void bulkSave(String indexName, String id,String value) throws IOException {
        // 获得客户端连接
        RestHighLevelClient esClient = getEsClient("hadoop101", 9200);
        BulkRequest bulkRequest = new BulkRequest(indexName);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.id(id) ;
        indexRequest.source(value, XContentType.JSON) ;
        bulkRequest.add(indexRequest) ;
        BulkResponse bulk = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        closeEsClient(esClient);
    }

    /**
     * 查询指定的字段
     */
    public static List<String> searchField(String indexName, String fieldName) throws IOException {
        // 获得客户端连接
        RestHighLevelClient esClient = getEsClient("hadoop101", 9200);

        // 判断索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!exists) {
            return null;
        }
        // 正常从es中提取指定字段
        List<String> result = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{fieldName}, null);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String value = (String) sourceAsMap.get(fieldName);
            result.add(value);
        }
        closeEsClient(esClient);
        return result;
    }

    public static void main(String[] args) throws Exception {
        List<String> mid = searchField("gmall_dau_info_2022-12-09", "channel");
        System.out.println(Arrays.toString(mid.toArray()));

    }
}
