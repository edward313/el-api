package com.easylearning.api.repository;

import com.easylearning.api.model.MonthlyPeriodDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MonthlyPeriodDetailRepository extends JpaRepository<MonthlyPeriodDetail, Long>, JpaSpecificationExecutor<MonthlyPeriodDetail> {
    List<MonthlyPeriodDetail> findAllByPeriodId(Long periodId);

    @Modifying
    @Transactional
    @Query("UPDATE MonthlyPeriodDetail m SET m.state = :newState WHERE m.period.id = :periodId")
    void updateStateByPeriodId(@Param("periodId") Long periodId, @Param("newState") Integer newState);

    @Modifying
    @Transactional
    void deleteAllByAccountId(Long accountId);

    @Modifying
    @Transactional
    void deleteAllByPeriodId(Long id);
}
