package com.easylearning.api.repository;

import com.easylearning.api.model.CourseVersioning;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.LessonVersioning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface LessonVersioningRepository extends JpaRepository<LessonVersioning, Long>, JpaSpecificationExecutor<LessonVersioning> {
    @Query(value = "SELECT * " +
            "FROM db_el_lesson_versioning lv " +
            "JOIN db_el_version v ON lv.version_id = v.id " +
            "WHERE lv.visual_id = :lessonId " +
            "ORDER BY v.version_code DESC " +
            "LIMIT 1;", nativeQuery = true)
    LessonVersioning findLatestLessonVersioningByLessonId(@Param("lessonId") Long lessonId);
    @Query(value = "SELECT lv.*\n" +
            "FROM db_el_lesson_versioning lv\n" +
            "JOIN db_el_version v ON lv.version_id = v.id\n" +
            "JOIN (\n" +
            "    SELECT lv.visual_id, MAX(v.version_code) AS max_version_code\n" +
            "    FROM db_el_lesson_versioning lv\n" +
            "    JOIN db_el_version v ON lv.version_id = v.id\n" +
            "    WHERE v.course_id = :courseId AND v.version_code <=:versionCode\n" +
            "    GROUP BY lv.visual_id\n" +
            ") AS max_versions ON lv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code\n" +
            "WHERE v.course_id = :courseId AND ((v.version_code < :versionCode and lv.status <> :ignoreStatus) ||  (v.version_code = :versionCode)) " +
            "ORDER BY ordering ASC;\n", nativeQuery = true)
    List<LessonVersioning> findAllLessonVersioningUpToVersionCode(@Param("courseId") Long courseId, @Param("versionCode") Integer versionCode, @Param("ignoreStatus") Integer ignoreStatus);
    @Query(value = "SELECT lv.*\n" +
            "FROM db_el_lesson_versioning lv\n" +
            "JOIN db_el_version v ON lv.version_id = v.id\n" +
            "JOIN (\n" +
            "    SELECT lv.visual_id, MAX(v.version_code) AS max_version_code\n" +
            "    FROM db_el_lesson_versioning lv\n" +
            "    JOIN db_el_version v ON lv.version_id = v.id\n" +
            "    WHERE v.course_id = :courseId AND v.version_code <=:versionCode\n" +
            "    GROUP BY lv.visual_id\n" +
            ") AS max_versions ON lv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code\n" +
            "WHERE v.course_id = :courseId AND (v.version_code <= :versionCode and lv.status <> :ignoreStatus) " +
            "ORDER BY ordering ASC;\n", nativeQuery = true)
    List<LessonVersioning> findAllLessonVersioningUpToVersionCodeAndNotDelete(@Param("courseId") Long courseId, @Param("versionCode") Integer versionCode, @Param("ignoreStatus") Integer ignoreStatus);
    @Query(value = "SELECT lv.*\n" +
            "FROM db_el_lesson_versioning lv\n" +
            "JOIN db_el_version v ON lv.version_id = v.id\n" +
            "JOIN db_el_course c ON lv.course_id = c.id\n" +
            "JOIN (\n" +
            "    SELECT lv.visual_id, MAX(v.version_code) AS max_version_code\n" +
            "    FROM db_el_lesson_versioning lv\n" +
            "    JOIN db_el_version v ON lv.version_id = v.id\n" +
            "    GROUP BY lv.visual_id\n" +
            ") AS max_versions ON lv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code\n" +
            "  WHERE lv.visual_id in :listIds and c.expert_id = :expertId and v.course_id = :courseId", nativeQuery = true)
    List<LessonVersioning> findAllHighestLessonVersioningByLessonIdsAndExpertId(@Param("listIds") List<Long> listIds,@Param("courseId") Long courseId, @Param("expertId") Long expertId);

    @Query(value = "SELECT lv.*\n" +
            "FROM db_el_lesson_versioning lv\n" +
            "JOIN db_el_version v ON lv.version_id = v.id\n" +
            "JOIN (\n" +
            "    SELECT lv.visual_id, MAX(v.version_code) AS max_version_code\n" +
            "    FROM db_el_lesson_versioning lv\n" +
            "    JOIN db_el_version v ON lv.version_id = v.id\n" +
            "    GROUP BY lv.visual_id\n" +
            ") AS max_versions ON lv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code\n" +
            "  WHERE lv.visual_id in :listIds and v.course_id = :courseId", nativeQuery = true)
    List<LessonVersioning> findAllHighestLessonVersioningByLessonIds(@Param("listIds") List<Long> listIds, @Param("courseId") Long courseId);

    List<LessonVersioning> findAllByVersionId(Long versionId);
    @Transactional
    @Modifying
    void deleteAllByCourseId(@Param("courseId") Long courseId);

    @Transactional
    @Modifying
    void deleteAllByVisualId(@Param("visualId") Long visualId);

    @Transactional
    @Modifying
    @Query(value =
            "DELETE FROM db_el_lesson_versioning " +
                    "WHERE course_id IN " +
                    "(SELECT id FROM db_el_course c WHERE c.expert_id = :expertId)",
            nativeQuery = true)
    void deleteAllLessonByExpertId(@Param("expertId") Long expertId);

    @Query(value =
            "SELECT * FROM db_el_lesson_versioning " +
                    "WHERE id = :id AND course_id IN " +
                    "(SELECT id FROM db_el_course c WHERE c.expert_id = :expertId) " +
                    "LIMIT 1",
            nativeQuery = true)
    Optional<LessonVersioning> findByIdAndExpertId(@Param("id") Long id, @Param("expertId") Long expertId);



    @Query(value = "SELECT lv.*\n" +
            "FROM db_el_lesson_versioning lv\n" +
            "JOIN db_el_version v ON lv.version_id = v.id\n" +
            "JOIN (\n" +
            "    SELECT lv.visual_id, MAX(v.version_code) AS max_version_code\n" +
            "    FROM db_el_lesson_versioning lv\n" +
            "    JOIN db_el_version v ON lv.version_id = v.id\n" +
            "    WHERE v.course_id = :courseId AND v.version_code <=:versionCode\n" +
            "    GROUP BY lv.visual_id\n" +
            ") AS max_versions ON lv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code\n" +
            "WHERE v.course_id = :courseId AND (v.version_code <= :versionCode and lv.status <> :ignoreStatus) AND lv.name = :name " +
            "ORDER BY ordering ASC LIMIT 1;\n", nativeQuery = true)
    LessonVersioning findFistLessonVersioningUpToVersionCodeByNameAndNotDelete(@Param("courseId") Long courseId, @Param("versionCode") Integer versionCode,
                                                                                       @Param("ignoreStatus") Integer ignoreStatus, @Param("name") String name);

}
