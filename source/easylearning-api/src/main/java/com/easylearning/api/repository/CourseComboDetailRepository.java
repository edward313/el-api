package com.easylearning.api.repository;

import com.easylearning.api.model.CourseComboDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CourseComboDetailRepository extends JpaRepository<CourseComboDetail, Long>, JpaSpecificationExecutor<CourseComboDetail> {
    List<CourseComboDetail> findAllByCourseId(Long course);
    CourseComboDetail findByCourseIdAndComboId(Long courseId, Long comboId);

    Boolean existsByComboIdAndCourseId(Long comboId, Long courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetail ccd WHERE ccd.course.id = :id OR ccd.combo.id = :id")
    void deleteAllByCourseIdOrComboId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetail ccd WHERE ccd.combo.id = :comboId AND ccd.combo.id NOT IN :courseIds")
    void deleteByComboIdsNotIn(@Param("courseIds") List<Long> courseIds,@Param("comboId") Long comboId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetail ccd WHERE ccd.course.id in (select id from Course where field.id = :categoryId) OR ccd.combo.id in (select id from Course where field.id = :categoryId)")
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetail ccd WHERE ccd.course.id in (select id from Course where expert.id = :accountId) OR ccd.combo.id in (select id from Course where expert.id = :accountId)")
    void deleteAllByAccountsId(@Param("accountId") Long accountId);
}
