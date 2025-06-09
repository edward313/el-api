package com.easylearning.api.repository;

import com.easylearning.api.model.CourseRetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CourseRetailRepository extends JpaRepository<CourseRetail, Long>, JpaSpecificationExecutor<CourseRetail> {
    Optional<CourseRetail> findFirstByCourseIdAndSellerId(Long courseId, Long sellerId);

    @Modifying
    @Transactional
    void deleteAllByCourseIdAndSellerId(Long courseId, Long sellerId);
    @Query("SELECT cr FROM CourseRetail cr JOIN FETCH cr.course c JOIN FETCH c.expert e WHERE cr.id = :id AND e.id = :expertId")
    Optional<CourseRetail> findFirstByIdAndExpertId(@Param("id") Long id, @Param("expertId") Long expertId);

    @Modifying
    @Transactional
    void deleteAllByCourseId(Long courseId);
    Optional<CourseRetail> findFirstBySeller_ReferralCode(String refCode);
    Optional<CourseRetail> findFirstByCourseIdAndSeller_ReferralCodeAndSeller_IsSellerAndSeller_Status(Long courseId, String refCode, Boolean isSeller, Integer status);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_retail " +
            "where seller_id =:accountId " +
            "or course_id in (select id from db_el_course where expert_id = :accountId)",nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_retail " +
            "where course_id in (select id from db_el_course " +
            "where category_id = :categoryId)",nativeQuery = true)
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);

    Integer countDistinctBySellerIdAndCourseIdIn(Long sellerId, List<Long> courseIds);
}