package com.easylearning.api.repository;


import com.easylearning.api.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    Page<Notification> findAllByIdUser(Long userId, Pageable pageable);

    Long countByIdUserAndState(Long userId, Integer state);

    Long countByIdUserAndStateAndKindIn(Long userId, Integer state, List<Integer> kinds);

    Page<Notification> findAllByIdUserAndState(Long userId, Integer state, Pageable pageable);

    List<Notification> findAllByIdUserAndState(Long userId, Integer state);

    @Transactional
    void deleteAllByIdUserAndKindIn(Long id, List<Integer> kinds);

    @Transactional
    void deleteAllByIdUser(Long id);

    @Transactional
    void deleteAllByIdUserAndKindAndRefId(Long idUser, Integer Kind, String refId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_notification  WHERE created_date <= (NOW() - INTERVAL :days DAY)", nativeQuery = true)
    void deleteNotificationsCreatedBeforeDays(@Param("days") Integer days);
}
