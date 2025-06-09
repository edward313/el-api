package com.easylearning.api.repository;

import com.easylearning.api.model.Completion;
import com.easylearning.api.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface CompletionRepository extends JpaRepository<Completion, Long>, JpaSpecificationExecutor<Completion> {

    Completion findFirstByStudentIdAndCourseIdAndLessonId(Long studentId, Long courseId, Long lessonId);

    @Query(value = "SELECT c.* FROM db_el_completion c " +
            "INNER JOIN db_el_lesson l ON c.lesson_id = l.id " +
            "WHERE c.student_id = :studentId " +
            "AND c.course_id = :courseId " +
            "AND c.lesson_id = :lessonId " +
            "AND l.kind <> :kind " +
            "LIMIT 1", nativeQuery = true)
    Completion findFirstByStudentIdAndCourseIdAndLessonIdAndExcludeLessonKind(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("lessonId") Long lessonId,
            @Param("kind") Integer kind);


    @Transactional
    @Modifying
    void deleteAllByStudentId(Long studentId);

    @Transactional
    @Modifying
    void deleteAllByCourseId(Long courseId);

    @Transactional
    @Modifying
    void deleteAllByLessonId(Long lessonId);

    @Transactional
    @Modifying
    void deleteAllByLessonIdIn(List<Long> lessonIds);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM db_el_completion WHERE course_id in (select id from db_el_course where category_id = :categoryId)", nativeQuery = true)
    void deleteAllCompletionByCategoryId(@Param("categoryId") Long categoryId);
}
