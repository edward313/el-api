package com.easylearning.api.repository;

import com.easylearning.api.model.CategoryHome;
import com.easylearning.api.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryHomeRepository extends JpaRepository<CategoryHome, Long>, JpaSpecificationExecutor<CategoryHome> {
    Optional<CategoryHome> findFirstByCourseId(Long courseId);
    @Query("SELECT COUNT(ch) FROM CategoryHome ch WHERE ch.category.id = :categoryId")
    Integer countByCategoryId(@Param("categoryId") Long categoryId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_category_home WHERE category_id = :categoryId ORDER BY created_date ASC LIMIT 1", nativeQuery = true)
    void deleteOldestCategoryHome(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT * FROM db_el_category_home ch " +
            "JOIN db_el_course c ON ch.course_id = c.id " +
            "WHERE ch.category_id = :categoryId " +
            "ORDER BY c.sold_quantity ASC, ch.created_date LIMIT 1", nativeQuery = true)
    Optional<CategoryHome> findTheLeastPurchasedAndOldestCategoryHome(@Param("categoryId") Long categoryId);
    @Query("SELECT ch.course FROM CategoryHome ch WHERE ch.category.id = :categoryId and ch.status = :status and ch.course.status = :courseStatus order by ch.course.soldQuantity desc")
    List<Course> findAllCourseByCategoryIdAndStatusAndCourseStatus(Long categoryId, Integer status, Integer courseStatus);

    @Query("SELECT ch.course FROM CategoryHome ch WHERE ch.category.id = :categoryId and ch.status = :status and ch.course.status = :courseStatus order by ch.createdDate desc")
    List<Course> findAllNewCourseByCategoryIdAndStatusAndCourseStatus(Long categoryId, Integer status, Integer courseStatus);

    CategoryHome findFirstByCategoryKindAndCourseId(Integer categoryKind, Long courseId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_category_home WHERE category_id = :categoryId OR course_id IN (SELECT id from db_el_course WHERE category_id = :categoryId)", nativeQuery = true)
    void deleteByCategory(@Param("categoryId") Long categoryId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_category_home WHERE course_id in (SELECT id from db_el_course where expert_id = :accountId)", nativeQuery = true)
    void deleteByAccount(@Param("accountId") Long accountId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_category_home WHERE course_id = :courseId", nativeQuery = true)
    void deleteByCourse(@Param("courseId") Long courseId);
    @Transactional
    @Modifying
    void deleteAllByCourseIdAndCategoryId(Long courseId, Long CategoryId);
    List<CategoryHome> findAllByCourseId(Long courseId);
    @Transactional
    @Modifying
    void deleteAllByCourseIdAndCategoryKindIn(Long courseId, List<Integer> kinds);
}
