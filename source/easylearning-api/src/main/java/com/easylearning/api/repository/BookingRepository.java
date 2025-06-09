package com.easylearning.api.repository;

import com.easylearning.api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    @Modifying
    @Transactional
    void deleteAllByStudentId(Long studentId);
    Optional<Booking> findFirstByCode(String code);

    @Modifying
    @Transactional
    void deleteAllByPromotionId(Long promotionId);
    Optional<Booking> findFirstByStudentIdAndPromotionId(Long studentId, Long promotionId);
    Optional<Booking> findFirstByStudentIdAndId(Long studentId, Long bookingId);
    Integer countByStudentIdAndState(Long studentId, Integer state);
}
