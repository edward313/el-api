package com.easylearning.api.repository;

import com.easylearning.api.model.CourseReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CourseReviewHistoryRepository extends JpaRepository<CourseReviewHistory, Long>, JpaSpecificationExecutor<CourseReviewHistory> {
    CourseReviewHistory findFirstByVersionIdAndState(Long versionId,Integer state);
    CourseReviewHistory findFirstByVersionIdAndStateOrderByDateDesc(Long versionId,Integer state);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_review_history where version_id in (select id from db_el_version where course_id =:courseId)", nativeQuery = true)
    void deleteAllByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_review_history where version_id " +
            "in (select id from db_el_version where course_id in (select id from db_el_course where expert_id = :accountId))", nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);
    void deleteByVersionIdAndState(@Param("versionId") Long versionId, @Param("state") Integer state);

    @Modifying
    @Transactional
    void deleteAllByVersionId(Long versionId);
}
