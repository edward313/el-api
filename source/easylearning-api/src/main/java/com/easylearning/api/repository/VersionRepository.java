package com.easylearning.api.repository;

import com.easylearning.api.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {
    @Query(value = "select * from db_el_version v where v.course_id = :courseId order by version_code desc limit 1", nativeQuery = true)
    Version findHighestVersionByCourseId(@Param("courseId") Long courseId);
    @Query(value = "select * from db_el_version v where v.course_id = :courseId and v.id <> :vesionId order by version_code desc limit 1", nativeQuery = true)
    Version findHighestVersionByCourseIdIgnoreId(@Param("courseId") Long courseId, @Param("vesionId") Long vesionId);
    @Query(value = "SELECT * FROM db_el_version v INNER JOIN db_el_lesson_versioning lv ON v.id = lv.version_id WHERE v.course_id = :courseId AND lv.visual_id = :lessonId ORDER BY v.version_code DESC LIMIT 1", nativeQuery = true)
    Version findHighestVersionByCourseIdAndLessonId(@Param("courseId") Long courseId, @Param("lessonId") Long lessonId);
    Optional<Version> findFirstByIdAndState(Long courseId, Integer State);
    @Modifying
    @Transactional
    @Query("delete from Version WHERE courseId = :courseId")
    void deleteAllByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value =
            "DELETE FROM db_el_version " +
                    "WHERE course_id IN " +
                    "(SELECT id FROM db_el_course c WHERE c.expert_id = :accountId)",
            nativeQuery = true)
    void deleteAllByAccountId(@Param("accountId") Long accountId);
}
