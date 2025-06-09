package com.easylearning.api.repository;

import com.easylearning.api.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Course findByName(String name);
    Course findFirstByNameAndExpertIdAndStatus(String name, Long expertId, Integer status);

    @Query(value = "SELECT * FROM db_el_course c " +
            "WHERE (:isFree = true AND c.price = 0) OR (:isFree = false AND c.price <> 0) " +
            "AND id not in (SELECT course_id FROM db_el_category_home WHERE category_id = :categoryId)" +
            "ORDER BY c.sold_quantity DESC LIMIT 1", nativeQuery = true)
    Course findCourseByTopSoldQuantityAndCategoryId(@Param("categoryId") Long categoryId, @Param("isFree") Boolean isFree);

    @Query("SELECT cc.course FROM CourseComboDetail cc WHERE cc.combo.id = :comboId")
    List<Course> findAllCourseByComboId(@Param("comboId") Long comboId);

    @Query("SELECT cc.course.id FROM CourseComboDetail cc WHERE cc.combo.id = :comboId")
    List<Long> findAllIdsCourseByComboId(@Param("comboId") Long comboId);

    @Query("SELECT cc.combo FROM CourseComboDetail cc WHERE cc.course.id = :courseId")
    List<Course> findAllComboByCourseId(@Param("courseId") Long courseId);

    Optional<Course> findByIdAndKindAndExpertIdAndStatus(Long id, Integer kind,Long expertId,Integer status);

    Optional<Course> findByIdAndStatus(Long id, Integer status);

    Optional<Course> findFirstByIdAndStatusNot(Long id, Integer ignoreStatus);
    @Query(value = "SELECT\n" +
            "  ((SELECT COUNT(DISTINCT c.lesson_id) FROM db_el_completion c\n" +
            "    INNER JOIN db_el_lesson l ON c.lesson_id = l.id\n" +
            "    WHERE c.course_id = :courseId AND c.is_finished = :isFinished AND c.student_id = :studentId AND l.kind <> :kind) /\n" +
            "  (SELECT COUNT(*) FROM db_el_lesson WHERE course_id = :courseId AND kind <> :kind)) * 100 AS ratio;\n", nativeQuery = true)
    Float getRatioCompleteLesson(@Param("courseId") Long courseId, @Param("studentId") Long studentId, @Param("kind") Integer kind, @Param("isFinished") Boolean isFinished);

    @Transactional
    @Modifying
    void deleteAllByExpertId(Long expertId);

    Optional<Course> findFirstByIdAndExpertId(Long id, Long expertId);
    @Transactional
    @Modifying
    void deleteAllByFieldId(Long fieldId);

    @Query(value = "SELECT * FROM db_el_course WHERE status = :status AND category_id = :categoryId AND id NOT IN (SELECT course_id FROM db_el_category_home WHERE category_id = :categoryId) ORDER BY created_date DESC LIMIT :limit", nativeQuery = true)
    List<Course> findAllByCourseNotInCategoryHomeTopNewWithKindOfCourse(@Param("categoryId") Long categoryId, @Param("status") Integer status, @Param("limit") Integer limit);
    @Query(value = "SELECT * FROM db_el_course WHERE status = :status AND id NOT IN (SELECT course_id FROM db_el_category_home WHERE category_id = :categoryId) ORDER BY created_date DESC LIMIT :limit", nativeQuery = true)
    List<Course> findAllByCourseNotInCategoryHomeTopNewWithKind(@Param("categoryId") Long categoryId, @Param("status") Integer status, @Param("limit") Integer limit);
    @Query(value = "SELECT * FROM db_el_course WHERE status = :status AND (:isFree = true AND price = 0 OR :isFree = false AND price > 0) AND category_id = :categoryId AND id NOT IN (SELECT course_id FROM db_el_category_home WHERE category_id = :categoryId) AND sold_quantity > 0 ORDER BY sold_quantity DESC LIMIT :limit", nativeQuery = true)
    List<Course> findAllByCourseNotInCategoryHomeTopBuyWithKind(@Param("categoryId") Long categoryId, @Param("isFree") Boolean isFree, @Param("status") Integer status, @Param("limit") Integer limit);

    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_course SET average_star = 0, total_review = 0, sold_quantity = 0 ", nativeQuery = true)
    void resetAverageStartAndTotalReviewAndSoldQuantity();

    @Query("SELECT c " +
            "FROM Course c " +
            "INNER JOIN CourseRetail cr ON cr.course.id = c.id " +
            "INNER JOIN Student s ON s.id = cr.seller.id " +
            "WHERE s.id =:sellerId AND c.id NOT IN :courseIds ")
    List<Course> findAllBySellerIdAndCourseIdNotIn(@Param("sellerId") Long sellerId, @Param("courseIds") List<Long> courseIds);

    @Query("SELECT c.id " +
            "FROM Course c " +
            "INNER JOIN CourseTransaction ct ON ct.course.id = c.id " +
            "INNER JOIN Booking b ON b.id = ct.booking.id " +
            "WHERE b.id =:bookingId")
    List<Long> findAllCourseIdByBookingId(@Param("bookingId") Long bookingId);

    @Query("select c.id from Course c where c.expert.id = :expertId and c.status = :status")
    List<Long> findAllIdByExpertIdAndStatus(@Param("expertId") Long expertId, @Param("status") Integer status);
}
