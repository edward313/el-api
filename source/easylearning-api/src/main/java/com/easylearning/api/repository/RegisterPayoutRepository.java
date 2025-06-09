package com.easylearning.api.repository;

import com.easylearning.api.model.RegisterPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RegisterPayoutRepository extends JpaRepository<RegisterPayout,Long>, JpaSpecificationExecutor<RegisterPayout> {
    @Query(value = "SELECT * FROM db_el_register_payout rp WHERE rp.account_id = :accountId AND rp.state IN :states ORDER BY rp.created_date DESC LIMIT 1", nativeQuery = true)
    RegisterPayout findByAccountIdOrderByCreateDateDescLimitOneAndInState(@Param("accountId") Long accountId, @Param("states") List<Integer> states);

    RegisterPayout findByIdAndAccountId(Long Id, Long accountId);

    @Query("SELECT rp FROM RegisterPayout rp WHERE " +
            "rp.createdDate >= :startDate AND rp.createdDate < :endDate " +
            "AND rp.state = :state " +
            "AND rp.kind = :kind")
    List<RegisterPayout> findAllByBetweenDateAndKind(@Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate,
                                                     @Param("state") Integer state,
                                                     @Param("kind") Integer kind);
    @Query("SELECT rp FROM RegisterPayout rp WHERE rp.account.id = :accountId " +
        "AND rp.createdDate >= :startDate AND rp.createdDate < :endDate " +
        "AND (:state IS NULL OR rp.state = :state)")
    List<RegisterPayout> findByAccountAndInPeriod(@Param("accountId") Long accountId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("state") Integer state);

    @Query(value = "SELECT * FROM db_el_register_payout rp WHERE rp.account_id = :accountId AND rp.end_date = :endDate AND rp.state = :state ORDER BY rp.created_date DESC LIMIT 1", nativeQuery = true)
    RegisterPayout findFirstByAccountAndInPeriodOrderByCreatedDate(@Param("accountId") Long accountId, @Param("endDate") Date endDate, @Param("state") Integer state);
    @Modifying
    @Transactional
    @Query("UPDATE RegisterPayout rp SET rp.state = :newState " +
            "WHERE rp.state = :currentState and rp.registerPeriod.id = :registerPeriodId")
    void updateStateByPeriodIdAndCurrentState(
            @Param("newState") Integer newState,
            @Param("currentState") Integer currentState,
            @Param("registerPeriodId") Long registerPeriodId);

    @Modifying
    @Transactional
    void deleteAllByRegisterPeriodId(Long registerPeriodId);
    RegisterPayout findFirstByAccountIdAndRegisterPeriodIdAndStateOrderByCreatedDateDesc(@Param("accountId") Long accountId,
                                                            @Param("payoutPeriodId") Long payoutPeriodId,
                                                            @Param("state") Integer state);
    RegisterPayout findFirstByStateOrderByCreatedDateDesc(@Param("state") Integer state);
    List<RegisterPayout> findAllByRegisterPeriodIdAndKind(Long registerPeriodId, Integer kind);
    RegisterPayout findFirstByRegisterPeriodIdAndAccountIdAndKind(Long registerPeriodId, Long accountId, Integer kind);
}
