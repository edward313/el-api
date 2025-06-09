package com.easylearning.api.repository;

import com.easylearning.api.model.CourseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CourseTransactionRepository extends JpaRepository<CourseTransaction, Long>, JpaSpecificationExecutor<CourseTransaction> {
    @Modifying
    @Transactional
    void deleteAllByCourseId(Long courseId);

    @Modifying
    @Transactional
    void deleteAllByBookingId(Long bookingId);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_transaction " +
            "where seller_id = :accountId " +
            "or expert_id = :accountId " +
            "or booking_id in (select id from db_el_booking where student_id = :accountId) " +
            "or course_id in (select id from db_el_course where expert_id = :accountId)",nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);
    List<CourseTransaction> findAllByBookingId(Long bookingId);
    @Query(value = "SELECT * FROM db_el_course_transaction ct " +
            "JOIN db_el_booking b ON ct.booking_id = b.id " +
            "JOIN db_el_course c ON ct.course_id = c.id " +
            "WHERE b.student_id = :studentId " +
            "AND c.id in :courseIds " +
            "AND (b.state = :bookingState1 OR b.state =:bookingState2) LIMIT 1", nativeQuery = true)
    CourseTransaction findFirstByCourseIdInAndStudentIdAndBookingStateOrBookingState(
            @Param("courseIds") List<Long> courseIds,
            @Param("studentId") Long studentId,
            @Param("bookingState1") Integer bookingState1,
            @Param("bookingState2") Integer bookingState2);

    @Query(value = "SELECT * FROM db_el_course_transaction ct " +
            "JOIN db_el_booking b ON ct.booking_id = b.id " +
            "JOIN db_el_course c ON ct.course_id = c.id " +
            "WHERE b.student_id = :studentId " +
            "AND c.id =:courseId " +
            "AND (b.state = :bookingState1 OR b.state =:bookingState2) LIMIT 1", nativeQuery = true)
    CourseTransaction findFirstByCourseIdAndStudentIdAndBookingStateOrBookingState(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId,
            @Param("bookingState1") Integer bookingState1,
            @Param("bookingState2") Integer bookingState2);

    @Query(value = "SELECT * FROM db_el_course_transaction ct " +
            "JOIN db_el_booking b ON ct.booking_id = b.id " +
            "JOIN db_el_course c ON ct.course_id = c.id " +
            "WHERE b.student_id = :studentId " +
            "AND c.id = :courseId " +
            "AND b.state = :bookingState LIMIT 1",
            nativeQuery = true)
    CourseTransaction findFirstByCourseIdInAndStudentIdAndBookingState(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId,
            @Param("bookingState") Integer bookingState
    );


    @Modifying
    @Transactional
    @Query(value = "delete from db_el_course_transaction " +
            "where course_id in (select id from db_el_course " +
            "where category_id = :categoryId)",nativeQuery = true)
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);
    @Query("SELECT ct FROM CourseTransaction ct WHERE ct.id IN :ids")
    List<CourseTransaction> findByListId(@Param("ids") Set<Long> ids);
}