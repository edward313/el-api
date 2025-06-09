package com.easylearning.api.repository;

import com.easylearning.api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findFirstByCourseIdAndStudentId(Long courseId, Long studentId);
    Optional<CartItem> findFirstByIdAndStudentId(Long Id, Long studentId);
    List<CartItem> findAllByStudentId(Long studentId);

    @Modifying
    @Transactional
    void deleteAllByCourseIdAndStudentId(Long courseId, Long studentId);

    @Modifying
    @Transactional
    void deleteAllByStudentId(Long studentId);

    @Modifying
    @Transactional
    void deleteAllByIdIn(List<Long> ids);

    @Modifying
    @Transactional
    @Query("delete from CartItem c where c.course.id in :courseIds and c.student.id = :studentId")
    void deleteAllByCourseIdInAndStudentId(@Param("courseIds") List<Long> courseIds, @Param("studentId") Long studentId);

    @Modifying
    @Transactional
    void deleteAllByCourseId(Long courseId);

    @Modifying
    @Transactional
    void deleteAllByStudentIdAndCourseId(Long studentId, Long courseId);

    @Modifying
    @Transactional
    @Query(value = "delete from db_el_cart_item " +
            "where student_id =:accountId " +
            "or course_id in (select id from db_el_course where expert_id = :accountId)",nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_cart_item WHERE course_id in (select id from db_el_course where category_id = :categoryId)", nativeQuery = true)
    void deleteAllCartItemByCategoryId(@Param("categoryId") Long categoryId);

    CartItem findFirstByStudentIdAndSellCodeIsNotNullOrderByTimeCreatedAsc(Long studentId);

    @Query(value = "SELECT c.* " +
            "FROM db_el_cart_item c " +
            "INNER JOIN db_el_student s ON s.account_id = c.student_id " +
            "WHERE s.account_id =:studentId AND c.sell_code IS NOT NULL AND c.sell_code !=:sellCode " +
            "ORDER BY c.time_created ASC LIMIT 1", nativeQuery = true)
    CartItem findFirstByStudentIdAndSellCodeIsNotNullAndSellCodeNot(Long studentId, String sellCode);

    @Transactional
    @Modifying
    @Query("UPDATE CartItem SET tempSellCode =:newTempSellCode WHERE student.id =:studentId AND tempSellCode =:currentTempSellCode")
    void updateAllTempSellCodeByNewTempSellCode(@Param("newTempSellCode") String newTempSellCode,
                                                @Param("studentId") Long studentId,
                                                @Param("currentTempSellCode") String currentTempSellCode);

    @Transactional
    @Modifying
    @Query("UPDATE CartItem SET tempSellCode =:newTempSellCode WHERE student.id =:studentId AND sellCode IS NULL AND tempSellCode IS NULL")
    void updateAllTempSellCode(@Param("studentId") Long studentId,
                               @Param("newTempSellCode") String newTempSellCode);

    @Transactional
    @Modifying
    @Query("UPDATE CartItem SET tempSellCode = NULL WHERE student.id =:studentId AND tempSellCode =:currentTempSellCode")
    void removeTempSellCode(@Param("studentId") Long studentId,
                            @Param("currentTempSellCode") String currentTempSellCode);
}
