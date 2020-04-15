package com.ruben.rubenesapi;

import com.alibaba.fastjson.JSON;
import com.ruben.rubenesapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RubenEsApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreatedIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("ruben_index");
        //2.执行创建请求
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }


    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("ruben_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("ruben_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    @Test
    void testAddDocument() throws IOException {
        User user = new User("ruben", 20);
        IndexRequest request = new IndexRequest("ruben_index");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }

    @Test
    void testIsExists() throws IOException {
        GetRequest request = new GetRequest("ruben_index", "1");
        //不获取返回的_source的上下文了
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("ruben_index", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        String source = response.getSourceAsString();
        System.out.println(source);
        System.out.println(response);
    }

    @Test
    void testUpdateRequest() throws IOException {
        UpdateRequest request = new UpdateRequest("ruben_index", "1");
        request.timeout("1s");
        User user = new User("rubenwe", 17);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest request = new DeleteRequest("ruben_index", "1");
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    @Test
    void testBulkRequest() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("1s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("ruben", 20));
        userList.add(new User("ruben", 30));
        userList.add(new User("ruben", 80));
        userList.add(new User("rubens", 17));
        userList.add(new User("rubens", 20));
        userList.add(new User("rubens", 26));
        //批处理请求
        for (int i = 0; i < userList.size(); i++) {
            request.add(new IndexRequest("ruben_index")
                    .id("" + (i + 1))
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.hasFailures());
    }

    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("ruben_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "ruben");       //精确查询
//        QueryBuilders.matchAllQuery();                //全局查询
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        System.out.println("==========================");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

}
