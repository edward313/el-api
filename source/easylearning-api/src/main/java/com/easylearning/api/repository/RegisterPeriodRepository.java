package com.easylearning.api.repository;

import com.easylearning.api.model.RegisterPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegisterPeriodRepository extends JpaRepository<RegisterPeriod, Long>, JpaSpecificationExecutor<RegisterPeriod> {
    RegisterPeriod findByIdAndState(Long id, Integer state);

    @Query(value = "SELECT * FROM db_el_register_period rp WHERE rp.state = :state ORDER BY rp.start_date ASC LIMIT 1", nativeQuery = true)
    RegisterPeriod findNearestByState(@Param("state") Integer state);
}
