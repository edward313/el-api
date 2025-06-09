package com.easylearning.api.repository;

import com.easylearning.api.model.ReferralExpertLog;
import com.easylearning.api.model.ReferralSellerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ReferralExpertLogRepository extends JpaRepository<ReferralExpertLog, Long>, JpaSpecificationExecutor<ReferralExpertLog> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ReferralExpertLog r WHERE r.expert.id = :expertId OR r.refExpert.id = :expertId")
    void deleteAllByExpertIdOrRefExpertId(@Param("expertId") Long expertId);

    ReferralExpertLog findFirstByExpertId(Long expertId);

    @Query(value = "SELECT * FROM db_el_referral_expert_log r WHERE r.ref_expert_id = :refExpertId " +
            "AND r.expert_id IN (SELECT expert_id FROM db_el_revenue_share rs " +
            "WHERE rs.course_transaction_id = :courseTransactionId AND rs.kind = :revenueShareKind)", nativeQuery = true)
    ReferralExpertLog findByRefExpertIdAndCourseTransactionId(Long refExpertId, Long courseTransactionId,Integer revenueShareKind);
}
