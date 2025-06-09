package com.easylearning.api.repository;

import com.easylearning.api.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionRepository extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {
    Promotion findFirstByName(String name);

    @Query("SELECT pc.promotion FROM PromotionCode pc WHERE pc.code = :promotionCode")
    Promotion findByPromotionCode(@Param("promotionCode") String promotionCode);
    @Modifying
    @Transactional
    @Query(value = "UPDATE db_el_promotion SET state = :runningState WHERE NOW() BETWEEN start_date AND end_date and state = :createdState", nativeQuery = true)
    void setRunningPromotionWhenValidTime(@Param("runningState") Integer runningState, @Param("createdState") Integer createdState);
    @Modifying
    @Transactional
    @Query(value = "UPDATE db_el_promotion SET state = :endState WHERE NOW() >= end_date and state = :runningState", nativeQuery = true)
    void setEndPromotionWhenExpiryTime(@Param("runningState") Integer runningState, @Param("endState") Integer endState);
}
