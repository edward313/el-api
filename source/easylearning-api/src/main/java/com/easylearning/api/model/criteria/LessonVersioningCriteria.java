package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Course;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.LessonVersioning;
import com.easylearning.api.model.Version;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class LessonVersioningCriteria {
    private Long id;
    private Integer status;
    private String name;
    private Integer duration;
    private Boolean isPreview;
    private Integer kind;
    private String content;
    private String urlDocument;
    private String description;
    private Integer ordering;
    private Long versionId;
    private Long visualId;
    private Long courseId;
    private Integer versionCode;
    private Boolean isIncludeLowerVersion = false;
    public Specification<LessonVersioning> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<LessonVersioning> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (!StringUtils.isEmpty(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
                }
                if (getDuration() != null) {
                    predicates.add(cb.equal(root.get("duration"), getDuration()));
                }
                if (getIsPreview() != null) {
                    predicates.add(cb.equal(root.get("isPreview"), getIsPreview()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (!StringUtils.isEmpty(getContent())) {
                    predicates.add(cb.like(cb.lower(root.get("content")), "%" + getContent().toLowerCase() + "%"));
                }
                if (!StringUtils.isEmpty(getUrlDocument())) {
                    predicates.add(cb.like(cb.lower(root.get("urlDocument")), "%" + getUrlDocument().toLowerCase() + "%"));
                }
                if (!StringUtils.isEmpty(getDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + getDescription().toLowerCase() + "%"));
                }
                if (getOrdering() != null) {
                    predicates.add(cb.equal(root.get("ordering"), getOrdering()));
                }
                if (getVersionId() != null && !getIsIncludeLowerVersion()) {
                    Join<Version, LessonVersioning> join = root.join("version", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getVersionId()));
                }
                if (getVersionId() != null && getIsIncludeLowerVersion()) {
                    Join<Version, LessonVersioning> join = root.join("version", JoinType.INNER);
                    predicates.add(cb.lessThanOrEqualTo(join.get("versionCode"), getVersionCode()));
                }
                if (getVersionCode() != null) {
                    Join<Version, LessonVersioning> join = root.join("version", JoinType.INNER);
                    predicates.add(cb.lessThanOrEqualTo(join.get("versionCode"), getVersionCode()));
                }
                if (getVisualId() != null) {
                    Join<LessonVersioning, Lesson> join = root.join("visual", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getVisualId()));
                }
                if (getCourseId() != null) {
                    Join<LessonVersioning, Version> join = root.join("version", JoinType.INNER);
                    predicates.add(cb.equal(join.get("courseId"), getCourseId()));
                }
                query.orderBy(cb.asc(root.get("ordering")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
