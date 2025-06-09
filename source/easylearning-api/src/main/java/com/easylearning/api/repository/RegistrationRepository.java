package com.easylearning.api.repository;

import com.easylearning.api.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long>, JpaSpecificationExecutor<Registration> {

    Boolean existsByCourse_ExpertIdAndStudentId(Long expertId, Long studentId);
    Registration findFirstByCourseIdAndStudentId(Long courseId, Long studentId);
    Registration findFirstByStudentIdAndStatusAndCourseStatus(Long studentId, Integer status, Integer courseStatus);

    @Query(value = "SELECT * FROM db_el_registration r " +
            "JOIN db_el_course c ON r.course_id = c.id " +
            " JOIN db_el_expert e ON c.expert_id = e.account_id " +
            "WHERE r.student_id = :studentId " +
            "AND e.account_id = :expertId LIMIT 1", nativeQuery = true)
    Registration findFirstByExpertIdAndStudentId(@Param("expertId") Long expertId,@Param("studentId") Long studentId);

    Registration findFirstByCourseIdInAndStudentId(List<Long> courseId, Long studentId);

    Integer countByCourseId(Long courseId);

    @Modifying
    @Transactional
    @Query("UPDATE Registration r SET r.isFinished = :isFinished WHERE r.student.id = :studentId AND r.course.id = :courseId")
    void updateRegistrationState(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("isFinished") Boolean isFinished);

    @Transactional
    @Modifying
    void deleteAllByStudentId(Long studentId);

    @Transactional
    @Modifying
    void deleteAllByCourseId(Long studentId);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_registration " +
            "where student_id = :accountId " +
            "or course_id in (select id from db_el_course where expert_id = :accountId)",nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_registration WHERE course_id in (select id from db_el_course where category_id = :categoryId)", nativeQuery = true)
    void deleteAllRegistrationByCategoryId(@Param("categoryId") Long categoryId);
}
