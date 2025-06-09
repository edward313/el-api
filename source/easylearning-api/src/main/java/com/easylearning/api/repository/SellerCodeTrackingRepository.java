package com.easylearning.api.repository;

import com.easylearning.api.model.SellerCodeTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SellerCodeTrackingRepository extends JpaRepository<SellerCodeTracking, Long>, JpaSpecificationExecutor<SellerCodeTracking> {
    Optional<SellerCodeTracking> findFirstByBrowserCode(String browserCode);

    Optional<SellerCodeTracking> findFirstByBrowserCodeAndSellCode(String browserCode, String sellCode);

    @Query(value = "select * from db_el_seller_code_tracking " +
            "where student_id is not null " +
            "and student_id = :studentId " +
            "and sell_code in (select sell_code from db_el_course_retail where course_id = :courseId) " +
            "order by created_date desc " +
            "LIMIT 1", nativeQuery = true)
    Optional<SellerCodeTracking> findLatestByStudentIdAndCourseId(Long studentId, Long courseId);

    @Modifying
    @Transactional
    @Query("delete from SellerCodeTracking s where s.student is not null and s.student.id = :accountId")
    void deleteAllByAccountId(@Param("accountId") Long accountId);
}
