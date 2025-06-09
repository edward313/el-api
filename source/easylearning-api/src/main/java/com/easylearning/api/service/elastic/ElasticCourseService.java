package com.easylearning.api.service.elastic;

import com.easylearning.api.model.criteria.CourseCriteria;
import com.easylearning.api.model.elastic.ElasticCourse;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElasticCourseService {
    @Value("${elastic.index.course}")
    private  String COURSE_INDEX;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    public void resetIndex(String index, Class<?> clazz) throws IOException {
        // Xóa index cũ
        restHighLevelClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);

        // Tạo lại mapping
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(index));
        indexOps.create();
        restHighLevelClient.indices().close(new CloseIndexRequest(index), RequestOptions.DEFAULT);
        String settingsJson = loadSettingsFromJsonFile();
        restHighLevelClient.indices().putSettings(
                new UpdateSettingsRequest(index).settings(settingsJson, XContentType.JSON),
                RequestOptions.DEFAULT
        );
        /* mapping index vừa tạo lại đúng với mapping của index đã xoá,
        không để hàm save tự tạo lại index sai khi index đang không tồn tại
        */
        indexOps.putMapping(indexOps.createMapping(clazz));
        // Áp dụng cài đặt từ file JSON

        // Mở lại index
        restHighLevelClient.indices().open(new OpenIndexRequest(index), RequestOptions.DEFAULT);

        log.error("Index " + index + " recreated successfully with explicit mappings and settings.");
    }

    private String loadSettingsFromJsonFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("/elastic-settings/name-reset.json");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    private QueryBuilder createSpanNearQuery(String field, String query) {
        String[] keywords = query.split("\\s+");
        SpanNearQueryBuilder spanNearQuery;
        spanNearQuery = QueryBuilders.spanNearQuery(new SpanTermQueryBuilder(field,keywords[0]),100)
                .inOrder(true);
        for(int i = 1;i<keywords.length;i++){
            spanNearQuery.addClause(new SpanTermQueryBuilder(field,keywords[i])).slop();
        }
        return spanNearQuery;
    }
    private SpanQueryBuilder createSpanFirstQuery(String field, String query) {
        String[] keywords = query.split("\\s+");
        SpanNearQueryBuilder spanNearQuery;
        spanNearQuery = QueryBuilders.spanNearQuery(new SpanTermQueryBuilder(field,keywords[0]),100)
                .inOrder(true);
        SpanTermQueryBuilder[] termQueries = new SpanTermQueryBuilder[keywords.length];
        for (int i = 1; i < keywords.length; i++) {
            termQueries[i] = QueryBuilders.spanTermQuery(field, keywords[i]);
            spanNearQuery.addClause(termQueries[i]);
        }
        return new SpanFirstQueryBuilder(spanNearQuery, keywords.length);
    }

    public Page<ElasticCourse> findAll(CourseCriteria courseCriteria, Pageable pageable) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // multiMatchQuery cho 2 field name
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("name", courseCriteria.getQuery().toLowerCase());
        boolQueryBuilder.must(queryBuilder);

        // ưu tiên kết quả gần nhau hơn so với query
        boolQueryBuilder.should(createSpanNearQuery("name", courseCriteria.getQuery().toLowerCase()));

        // chọn kết quả bắt đầu bằng query
        boolQueryBuilder.should(createSpanFirstQuery("name", courseCriteria.getQuery().toLowerCase()));

        if (courseCriteria.getFieldId() != null) {
            QueryBuilder categoryIdQuery = QueryBuilders.matchQuery("field.categoryId", courseCriteria.getFieldId());
            boolQueryBuilder.filter(categoryIdQuery);
        }
        if (courseCriteria.getStatus() != null) {
            QueryBuilder statusQuery = QueryBuilders.matchQuery("status", courseCriteria.getStatus());
            boolQueryBuilder.filter(statusQuery);
        }
        if (courseCriteria.getIsFree() != null) {
            boolQueryBuilder.filter(
                    courseCriteria.getIsFree()
                            ? QueryBuilders.matchQuery("price", 0)
                            : QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("price", 0))
            );
        }

        SortBuilder<?> sortBuilder = SortBuilders.scoreSort(); // mặc định là score sort
        if (courseCriteria.getSearchType() != null) {
            switch (courseCriteria.getSearchType()) {
                case 1:
                    sortBuilder = SortBuilders.fieldSort("totalReview").order(SortOrder.DESC);
                    break;
                case 2:
                    sortBuilder = SortBuilders.fieldSort("createdDate").order(SortOrder.DESC);
                    break;
                default:
            }
        }
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageable)
                .withSort(sortBuilder)
                .build();

        SearchHits<ElasticCourse> courseHits = elasticsearchOperations
                .search(searchQuery, ElasticCourse.class, IndexCoordinates.of(COURSE_INDEX));
        // Extract and return the results as a Page
        List<ElasticCourse> content = courseHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, courseHits.getTotalHits());
    }
}
