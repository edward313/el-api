package com.easylearning.api.repository;

import com.easylearning.api.model.PromotionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PromotionCodeRepository extends JpaRepository<PromotionCode, Long>, JpaSpecificationExecutor<PromotionCode> {
    Optional<PromotionCode> findByCode(String code);
    List<PromotionCode> findAllByPromotionId(Long promotionId);

    @Modifying
    @Transactional
    void deleteAllByPromotionId(Long promotionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PromotionCode pc WHERE pc.promotion.id IN (SELECT p.id FROM Promotion p WHERE p.id IN (SELECT b.promotion.id FROM Booking b WHERE b.id = :bookingId))")
    void deleteAllByBookingId(@Param("bookingId") Long bookingId);
    @Query(value = "SELECT * FROM db_el_promotion_code pc " +
            "JOIN db_el_promotion pr ON pc.promotion_id = pr.id " +
            "WHERE pc.code = :code " +
            "AND pc.status = :status " +
            "AND pr.status = :promotionStatus " +
            "AND pr.state = :promotionState " +
            "AND pr.start_date <= NOW() " +
            "AND pr.end_date >= NOW() " +
            "LIMIT 1", nativeQuery = true)
    Optional<PromotionCode> findValidPromotionCodeByCode(@Param("code") String code,
                                                         @Param("status") Integer status,
                                                         @Param("promotionState") Integer promotionState,
                                                         @Param("promotionStatus") Integer promotionStatus);
}
