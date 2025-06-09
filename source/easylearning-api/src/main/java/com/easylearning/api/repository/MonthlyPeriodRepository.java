package com.easylearning.api.repository;

import com.easylearning.api.model.MonthlyPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface MonthlyPeriodRepository extends JpaRepository<MonthlyPeriod, Long>, JpaSpecificationExecutor<MonthlyPeriod> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_monthly_period " +
            "SET system_revenue = ROUND(( " +
            "    SELECT COALESCE(SUM(price), 0) " +
            "    FROM db_el_course_transaction " +
            "    WHERE id IN (" +
            "        SELECT course_transaction_id " +
            "        FROM db_el_revenue_share " +
            "        WHERE monthly_period_id = :monthlyPeriodId" +
            "    )" +
            ") - ( " +
            "    SELECT COALESCE(SUM(sale_off_money), 0) " +
            "    FROM db_el_booking " +
            "    WHERE id IN ( " +
            "        SELECT DISTINCT booking_id " +
            "        FROM db_el_course_transaction " +
            "        WHERE id IN ( " +
            "            SELECT course_transaction_id " +
            "            FROM db_el_revenue_share " +
            "            WHERE monthly_period_id = :monthlyPeriodId" +
            "        )" +
            "    )" +
            ") - :totalMonthlyPeriodTotalPayout, :decimalPlaces) " +
            "WHERE id = :monthlyPeriodId", nativeQuery = true)
    void calculateSystemRevenueShareMoney(@Param("monthlyPeriodId") Long monthlyPeriodId, @Param("totalMonthlyPeriodTotalPayout") Double totalMonthlyPeriodTotalPayout, @Param("decimalPlaces") Integer decimalPlaces);




    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_booking " +
            "SET payout_status = :payoutStatus " +
            "WHERE id IN (" +
            "    SELECT DISTINCT booking_id " +
            "    FROM db_el_course_transaction " +
            "    WHERE id IN (" +
            "        SELECT course_transaction_id " +
            "        FROM db_el_revenue_share " +
            "        WHERE created_date BETWEEN :startDate AND :endDate" +
            "    )" +
            ")",
            nativeQuery = true)
    void updateBookingState(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("payoutStatus") Integer payoutStatus);


    MonthlyPeriod findByIdAndState(Long id, Integer state);

    @Query(value = "SELECT * FROM db_el_monthly_period pp WHERE pp.state = :state ORDER BY pp.start_date ASC LIMIT 1", nativeQuery = true)
    MonthlyPeriod findNearestByState(@Param("state") Integer state);

    Boolean existsByStartDateGreaterThanEqualAndEndDateLessThanEqual(Date startDate, Date endDate);

    MonthlyPeriod findFirstByStartDateGreaterThanEqualAndEndDateLessThanEqual(Date startDate, Date endDate);
}
