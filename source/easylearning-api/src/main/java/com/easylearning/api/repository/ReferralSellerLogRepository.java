package com.easylearning.api.repository;

import com.easylearning.api.model.ReferralSellerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface ReferralSellerLogRepository extends JpaRepository<ReferralSellerLog,Long>, JpaSpecificationExecutor<ReferralSellerLog> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ReferralSellerLog r WHERE r.student.id = :studentId OR r.refStudent.id = :studentId")
    void deleteAllByStudentIdOrRefStudentId(@Param("studentId") Long studentId);
    ReferralSellerLog findFirstByStudentId(Long studentId);

    @Query(value = "SELECT * FROM db_el_referral_seller_log r WHERE r.ref_student_id = :refStudentId " +
            "AND r.student_id IN (SELECT seller_id FROM db_el_revenue_share rs " +
            "WHERE rs.course_transaction_id = :courseTransactionId AND rs.kind = :revenueShareKind)", nativeQuery = true)
    ReferralSellerLog findByRefStudentIdAndCourseTransactionId(Long refStudentId, Long courseTransactionId, Integer revenueShareKind);


    ReferralSellerLog findFirstByRefStudentIdAndStudentId(Long refStudentId, Long studentId);
}
