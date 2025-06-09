package com.easylearning.api.repository;

import com.easylearning.api.dto.review.AmountReviewDetailDto;
import com.easylearning.api.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    Boolean existsByCourseIdAndStudentIdAndKind(Long courseId, Long studentId, Integer kind);

    Boolean existsByExpertIdAndStudentIdAndKind(Long expertId, Long studentId, Integer kind);
    Boolean existsByStudentIdAndKind(Long studentId, Integer kind);
    Optional<Review> findByIdAndStudentId(Long id, Long studentId);
    Page<Review> findAllByCourseIdAndStatus(Long courseId, Integer status, Pageable pageable);
    Page<Review> findAllByExpertIdAndStatus(Long expertId, Integer status, Pageable pageable);
    List<Review> findByCourseIdAndStudentId(Long courseId, Long studentId);
    @Modifying
    @Transactional
    @Query(value = "delete from db_el_review " +
            "where student_id = :accountId " +
            "or course_id in (select id from db_el_course where expert_id = :accountId)",nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);
    @Transactional
    @Modifying
    void deleteAllByCourseId(Long courseId);

    @Transactional
    @Modifying
    @Query(value = "delete from db_el_review " +
            "where course_id in (select id from db_el_course where category_id = :categoryId)",nativeQuery = true)
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);
    @Query("SELECT new com.easylearning.api.dto.review.AmountReviewDetailDto(r.star, COUNT(r)) FROM Review r WHERE r.course.id = :courseId AND r.kind = :kind GROUP BY r.star")
    List<AmountReviewDetailDto> groupByStar(@Param("courseId") Long courseId, @Param("kind") Integer kind);

    @Query("SELECT new com.easylearning.api.dto.review.AmountReviewDetailDto(r.star, COUNT(r)) FROM Review r WHERE r.expert.id = :expertId AND r.kind = :kind GROUP BY r.star")
    List<AmountReviewDetailDto> groupByExpertStar(@Param("expertId") Long expertId, @Param("kind") Integer kind);

    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_course " +
            "SET average_star = COALESCE((average_star * total_review - " +
            "(SELECT r.star FROM db_el_review r WHERE r.course_id = db_el_course.id AND r.student_id = :studentId AND r.kind = :reviewKind)) / NULLIF((total_review - 1), 0), 0), " +
            "total_review = total_review - 1 " +
            "WHERE id IN (" +
            "    SELECT course_id " +
            "    FROM db_el_review " +
            "    WHERE student_id = :studentId AND kind = :reviewKind" +
            ")", nativeQuery = true)
    void updateCourseAverageStarAndTotalReview(@Param("studentId") Long studentId, @Param("reviewKind") Integer reviewKind);
    Page<Review> findAllByCourseIdAndStatusAndKind(Long courseId, Integer status,Integer kind, Pageable pageable);
    Page<Review> findAllByExpertIdAndStatusAndKind(Long expertId, Integer status, Integer kind, Pageable pageable);

    void deleteByKindNot(Integer kind);
}
