package com.easylearning.api.repository;

import com.easylearning.api.model.CourseComboDetailVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CourseComboDetailVersionRepository extends JpaRepository<CourseComboDetailVersion, Long>, JpaSpecificationExecutor<CourseComboDetailVersion> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetailVersion ccdv WHERE ccdv.combo.id = :comboId and ccdv.course.id not in :courseIds")
    void deleteByComboIdAndNotInCourseIds(@Param("courseIds") List<Long> courseIds, @Param("comboId") Long comboId);
    Boolean existsByComboIdAndCourseId(Long comboId, Long courseId);
    @Query("SELECT cc FROM CourseComboDetailVersion cc WHERE cc.combo.id = :comboId")
    List<CourseComboDetailVersion> findAllByComboId(@Param("comboId") Long comboId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetailVersion ccd WHERE ccd.course.id = :id OR ccd.combo.id in (select cv.id from CourseVersioning cv where cv.visualId = :id)")
    void deleteAllByCourseId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseComboDetailVersion ccd WHERE ccd.course.id in (select id from Course where expert.id = :accountId) OR ccd.combo.id in (select id from CourseVersioning where expert.id = :accountId)")
    void deleteAllByAccountsId(@Param("accountId") Long accountId);
}
