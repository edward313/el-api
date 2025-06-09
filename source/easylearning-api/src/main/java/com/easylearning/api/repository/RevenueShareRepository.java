package com.easylearning.api.repository;

import com.easylearning.api.dto.revenueShare.MyRevenueDtoInterface;
import com.easylearning.api.model.RevenueShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RevenueShareRepository extends JpaRepository<RevenueShare, Long>, JpaSpecificationExecutor<RevenueShare> {

    @Query(value = "SELECT SUM(rs.revenue_money) FROM db_el_revenue_share rs WHERE (rs.expert_id = :accountId OR rs.seller_id = :accountId) AND rs.status = :status AND rs.payout_status = :payoutStatus", nativeQuery = true)
    Double sumRevenueMoneyByExpertIdOrSellerIdAndStatusAndPayoutStatus(@Param("accountId") Long accountId, @Param("status") Integer status, @Param("payoutStatus") Integer payoutStatus);

    @Query(value = "SELECT * FROM db_el_revenue_share rs WHERE (rs.payout_status = :payoutStatus OR :payoutStatus IS NULL) AND (rs.expert_id = :accountId OR rs.seller_id = :accountId) ORDER BY rs.created_date ASC LIMIT 1", nativeQuery = true)
    RevenueShare findByExpertIdOrSellerIdOrderByCreatedDateAscLimitOne(@Param("accountId") Long accountId, @Param("payoutStatus") Integer payoutStatus);

    @Query(value = "SELECT * FROM db_el_revenue_share rs WHERE (rs.payout_status = :payoutStatus OR :payoutStatus IS NULL) ORDER BY rs.created_date ASC LIMIT 1", nativeQuery = true)
    RevenueShare findByPayoutStatusOrderByCreatedDateAscLimitOne(@Param("payoutStatus") Integer payoutStatus);

    @Query(value = "SELECT * FROM db_el_revenue_share rs WHERE " +
            "rs.created_date BETWEEN :startDate AND :endDate AND " +
            "rs.payout_status = :payoutStatus",
            nativeQuery = true)
    List<RevenueShare> findAllRevenueShareBetweenAndPayoutStatus(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("payoutStatus") Integer payoutStatus);

    @Modifying
    @Transactional
    void deleteAllByCourseTransactionId(Long courseTransactionId);

    @Modifying
    @Transactional
    @Query(value =
            "DELETE FROM db_el_revenue_share " +
                    "WHERE course_transaction_id IN " +
                    "(SELECT id FROM db_el_course_transaction ct " +
                    "WHERE course_id = :courseId)", nativeQuery = true)
    void deleteAllByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value =
            "DELETE FROM db_el_revenue_share " +
                    "WHERE course_transaction_id IN " +
                    "(SELECT ct.id FROM db_el_course_transaction ct " +
                    "WHERE ct.course_id IN (SELECT c.id FROM db_el_course c WHERE c.category_id = :categoryId))", nativeQuery = true)
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM db_el_revenue_share WHERE course_transaction_id IN " +
            "(SELECT id FROM db_el_course_transaction WHERE booking_id = :bookingId)", nativeQuery = true)
    void deleteAllByBookingId(@Param("bookingId") Long bookingId);


    @Query(value = "SELECT " +
            "  (SELECT COALESCE(SUM(wt.money), 0) FROM db_el_wallet_transaction wt " +
            "   INNER JOIN db_el_wallet w ON w.id = wt.wallet_id " +
            "   WHERE wt.kind = :transactionKind AND w.account_id = :accountId " +
            "   AND wt.created_date >= :threeMonthsAgoDate) AS totalPayoutMoney, " +
            "   \n" +
            "  (SELECT COALESCE(SUM(revenue_money), 0) FROM db_el_revenue_share " +
            "   WHERE (expert_id = :accountId OR seller_id = :accountId) " +
            "   AND created_date >= :threeMonthsAgoDate" +
            "   AND payout_status =:payoutStatus) AS totalMoney", nativeQuery = true)
    MyRevenueDtoInterface getMyRevenueDto(@Param("accountId") Long accountId, @Param("transactionKind") Integer transactionKind, @Param("threeMonthsAgoDate") Date threeMonthsAgoDate, @Param("payoutStatus") Integer payoutStatus);
    @Modifying
    @Transactional
    @Query(value =
            "DELETE FROM db_el_revenue_share " +
                    "WHERE course_transaction_id IN " +
                    "(SELECT id FROM db_el_course_transaction ct " +
                    "WHERE seller_id = :accountId " +
                    "OR expert_id = :accountId " +
                    "OR booking_id IN (SELECT id FROM db_el_booking WHERE student_id = :accountId) " +
                    "OR course_id IN (SELECT id FROM db_el_course WHERE expert_id = :accountId))",
            nativeQuery = true)
    void deleteAllRevenueShareByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE db_el_revenue_share rs " +
            "SET rs.payout_status = :payoutStatus " +
            "WHERE rs.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    void updatePayoutStatusBetween(@Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate,
                                   @Param("payoutStatus") Integer payoutStatus);

    @Modifying
    @Transactional
    @Query("UPDATE RevenueShare SET payoutStatus =:payoutStatus WHERE createdDate >= :startDate AND createdDate <= :endDate")
    void updateAllByPayoutStatusAndCreatedDateBetweenStartDateAndEndDate(@Param("payoutStatus") Integer payoutStatus, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
