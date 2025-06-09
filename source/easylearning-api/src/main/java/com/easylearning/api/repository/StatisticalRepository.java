package com.easylearning.api.repository;

import com.easylearning.api.dto.statistical.FeStatisticDto;
import com.easylearning.api.model.Statistical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatisticalRepository extends JpaRepository<Statistical, Long>, JpaSpecificationExecutor<Statistical> {
    Statistical findFirstByStatisticalKey(String key);
    @Query(value = "SELECT \n" +
            "    (SELECT COUNT(*) FROM db_el_expert WHERE status = :status) AS totalExpert,\n" +
            "    (SELECT COUNT(*) FROM db_el_student WHERE status = :status) AS totalStudent,\n" +
            "    (SELECT COUNT(*) FROM db_el_course WHERE status = :status and kind = :courseKind) AS totalCourse,\n" +
            "    (SELECT COUNT(*) FROM db_el_registration WHERE status = :status) AS totalCourseSell;\n",nativeQuery = true)
    FeStatisticDto getFeStatistic(@Param("status") Integer status,@Param("courseKind") Integer courseKind);
}
