package com.easylearning.api.repository;

import com.easylearning.api.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
    Optional<Lesson> findByName(String name);

    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN FETCH l.course c " +
            "LEFT JOIN FETCH c.expert e " +
            "WHERE l.id = :id AND e.id = :expertId")
    Optional<Lesson> findByIdAndExpertId(@Param("id") Long id, @Param("expertId") Long expertId);

    @Query("SELECT l FROM Lesson l " +
            "JOIN l.course c " +
            "JOIN Registration r ON c.id = r.course.id " +
            "WHERE l.id = :id " +
            "AND r.student.id = :studentId")
    Optional<Lesson> findByIdAndStudentId(@Param("id") Long id, @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN FETCH l.course c " +
            "LEFT JOIN FETCH c.expert e " +
            "WHERE l.id = :id AND c.expert.id = :expertId AND l.kind = :kind")
    Optional<Lesson> findByIdAndExpertIdAndKind(@Param("id") Long id, @Param("expertId") Long expertId, @Param("kind") Integer kind);
    List<Lesson> findAllByCourseIdAndStatusOrderByOrdering(Long courseId,Integer status);
    Lesson findByIdAndKind(Long id,Integer kind);
    @Query("SELECT l.urlDocument FROM Lesson l WHERE l.course.id = :courseId")
    List<String> getAllUrlDocumentByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT l.urlDocument FROM Lesson l WHERE l.course.expert.id = :expertId")
    List<String> getAllUrlDocumentByExpertId(@Param("expertId") Long expertId);

    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.course.id = :courseId")
    Optional<Lesson> findByIdAndCourseId(@Param("id") Long id, @Param("courseId") Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.course.status <> :ignoreCourseStatus")
    Optional<Lesson> findByIdAndIgnoreCourseStatus(@Param("id") Long id, @Param("ignoreCourseStatus") Integer ignoreCourseStatus);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId AND l.kind <> :kind")
    Long countByCourseIdExcludeSection(@Param("courseId") Long courseId, @Param("kind") Integer kind);

    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.course.id = :courseId AND l.kind <> :kind")
    Optional<Lesson> findByIdAndCourseIdExcludeKind(@Param("id") Long id, @Param("courseId") Long courseId,  @Param("kind") Integer kind);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_lesson WHERE course_id = :courseId", nativeQuery = true)
    void deleteAllLessonByCourseId(@Param("courseId") Long courseId);


    @Transactional
    @Modifying
    @Query(value =
            "DELETE FROM db_el_lesson " +
                    "WHERE course_id IN " +
                    "(SELECT id FROM db_el_course c WHERE c.expert_id = :expertId)",
            nativeQuery = true)
    void deleteAllLessonByExpertId(@Param("expertId") Long expertId);


    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN FETCH l.course c " +
            "LEFT JOIN FETCH c.expert e " +
            "WHERE l.id in :id AND c.expert.id = :expertId")
    List<Lesson> findAllByIdAndExpertId(@Param("id") List<Long> id, @Param("expertId") Long expertId);

    Optional<Lesson> findFirstByIdAndIsPreview(Long id, Boolean isPreview);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_lesson WHERE course_id in (select id from db_el_course where category_id = :categoryId)", nativeQuery = true)
    void deleteAllLessonByCategoryId(@Param("categoryId") Long categoryId);

    @Transactional
    @Modifying
    void deleteAllByIdIn(@Param("ids") List<Long> ids);
}
