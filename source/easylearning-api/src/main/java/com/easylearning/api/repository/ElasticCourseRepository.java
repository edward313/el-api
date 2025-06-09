package com.easylearning.api.repository;

import com.easylearning.api.model.elastic.ElasticCourse;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticCourseRepository extends ElasticsearchRepository<ElasticCourse, String> {
    ElasticCourse findByCourseId(Long courseId);
    void deleteByCourseId(Long courseId);
}

