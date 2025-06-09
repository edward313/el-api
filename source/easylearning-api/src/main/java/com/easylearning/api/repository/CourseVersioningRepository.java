package com.easylearning.api.repository;

import com.easylearning.api.model.CourseVersioning;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CourseVersioningRepository extends JpaRepository<CourseVersioning, Long>, JpaSpecificationExecutor<CourseVersioning> {
    CourseVersioning findFirstByVersionId(Long versionId);

    CourseVersioning findByVisualIdAndVersionId(Long visualId, Long versionId);
    @Query(value = "SELECT * " +
            "FROM db_el_course_versioning cv " +
            "JOIN db_el_version v ON cv.version_id = v.id " +
            "WHERE cv.visual_id = :courseId " +
            "ORDER BY v.version_code DESC " +
            "LIMIT 1;", nativeQuery = true)
    CourseVersioning findLatestCourseVersioningByCourseId(@Param("courseId") Long courseId);
    @Query(value = "SELECT * " +
            "FROM db_el_course_versioning cv " +
            "JOIN db_el_version v ON cv.version_id = v.id " +
            "WHERE cv.visual_id = :courseId and v.version_code <= :versionCode " +
            "ORDER BY v.version_code DESC " +
            "LIMIT 1;", nativeQuery = true)
    CourseVersioning findLatestCourseVersioningUpToVersionCode(@Param("courseId") Long courseId,@Param("versionCode") Integer versionCode);

    @Query(value = "SELECT * " +
            "FROM db_el_course_versioning cv " +
            "JOIN db_el_version v ON cv.version_id = v.id " +
            "WHERE cv.visual_id = :courseId and v.state =:state " +
            "ORDER BY v.version_code DESC " +
            "LIMIT 1;", nativeQuery = true)
    CourseVersioning findLatestCourseVersioningByCourseIdAndState(@Param("courseId") Long courseId,@Param("state") Integer state);

    @Query(value = "SELECT cv.* FROM db_el_course_versioning cv " +
            "JOIN db_el_version v ON cv.version_id = v.id " +
            "JOIN ( " +
            "    SELECT cv.visual_id, MAX(v.version_code) AS max_version_code " +
            "    FROM db_el_course_versioning cv " +
            "    JOIN db_el_version v ON cv.version_id = v.id " +
            "    GROUP BY cv.visual_id " +
            ") AS max_versions ON cv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code", nativeQuery = true)
    List<CourseVersioning> findAllLastestCourseVersioning();

    @Query(value = "SELECT COUNT(*) > 0 FROM db_el_course_versioning cv " +
            "JOIN db_el_version v ON cv.version_id = v.id " +
            "JOIN ( " +
            "    SELECT cv.visual_id, MAX(v.version_code) AS max_version_code " +
            "    FROM db_el_course_versioning cv " +
            "    JOIN db_el_version v ON cv.version_id = v.id " +
            "    GROUP BY cv.visual_id " +
            ") AS max_versions ON cv.visual_id = max_versions.visual_id AND v.version_code = max_versions.max_version_code " +
            "WHERE cv.name = :nameToUpdate AND cv.expert_id = :expertId  AND (:courseIdToUpdate IS NULL OR cv.visual_id <> :courseIdToUpdate) and cv.status <> :ignoreStatus", nativeQuery = true)
    Integer countCourseVersioningWithSameName(@Param("nameToUpdate") String nameToUpdate,
                                              @Param("courseIdToUpdate") Long courseIdToUpdate,
                                              @Param("ignoreStatus") Integer ignoreStatus,
                                              @Param("expertId") Long expertId);

    @Transactional
    @Modifying
    void deleteAllByVisualId(Long courseId);

    @Transactional
    @Modifying
    void deleteAllByExpertId(Long expertId);
}
