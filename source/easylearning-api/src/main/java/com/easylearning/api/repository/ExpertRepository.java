package com.easylearning.api.repository;

import com.easylearning.api.model.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExpertRepository extends JpaRepository<Expert, Long>, JpaSpecificationExecutor<Expert> {

    Expert findByReferralCode(String code);

    Expert findByReferralCodeAndStatus(String code, Integer status);

    Expert findByReferralCodeAndStatusAndAccountStatus(String code, Integer status, Integer accountStatus);

    @Query("SELECT e FROM Expert e WHERE e.account.phone = :phone")
    Expert findExpertByPhone(@Param("phone") String phone);

    Expert findFirstByIsSystemExpert(Boolean isSystemExpert);

    Expert findFirstByIsOutstandingOrderByOrderingDesc(Boolean isOutstanding);

    Expert findByIdAndIsSystemExpert(Long id, Boolean isSystemExpert);

    @Transactional
    @Modifying
    void deleteAllByAccountId(Long accountId);

    @Modifying
    @Transactional
    @Query(value =
            "UPDATE db_el_expert e " +
                    "SET e.total_course = e.total_course - (SELECT COUNT(c.id) FROM db_el_course c WHERE c.expert_id = e.account_id AND c.category_id = :categoryId), " +
                    "    e.total_lesson_time = e.total_lesson_time - (SELECT SUM(c.total_study_time) FROM db_el_course c WHERE c.expert_id = e.account_id AND c.category_id = :categoryId), " +
                    "    e.total_student = e.total_student - (SELECT SUM(c.sold_quantity) FROM db_el_course c WHERE c.expert_id = e.account_id AND c.category_id = :categoryId) " +
                    "WHERE EXISTS (SELECT 1 FROM db_el_course c WHERE c.expert_id = e.account_id AND c.category_id = :categoryId)", nativeQuery = true)
    void updateExpertsAfterDeletingCoursesByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE db_el_expert e " +
            "SET e.total_student = e.total_student - 1 " +
            "WHERE EXISTS ( " +
            "    SELECT 1 " +
            "    FROM db_el_registration r " +
            "    WHERE r.student_id = :studentId " +
            "    AND r.course_id IN (SELECT c.id FROM db_el_course c WHERE c.expert_id = e.account_id) " +
            ")", nativeQuery = true)
    void updateExpertsAfterDeleteStudent(@Param("studentId") Long studentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_expert SET total_student = 0", nativeQuery = true)
    void resetTotalStudent();
}
