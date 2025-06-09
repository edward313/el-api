package com.easylearning.api.model.criteria;


import com.easylearning.api.model.Course;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.Lesson;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LessonCriteria implements Serializable {
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
    private Long courseId;
    private Boolean isActive;
    private Integer ignoreCourseStatus;
    private Long expertId;
    private Integer ignoreStatus;
    public Specification<Lesson> getSpecification() {
        return new Specification<Lesson>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Lesson> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getIgnoreStatus() != null) {
                    predicates.add(cb.notEqual(root.get("status"), getIgnoreStatus()));
                }
                if(getIgnoreCourseStatus() !=null){
                    Join<Lesson, Course> joinExpert = root.join("course", JoinType.INNER);
                    predicates.add(cb.notEqual(joinExpert.get("status"), getIgnoreCourseStatus()));
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
                if(getCourseId() !=null){
                    Join<Lesson, Course> joinExpert = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getCourseId()));
                }
                if (getExpertId() != null) {
                    Join<Lesson, Course> joinCourse = root.join("course", JoinType.INNER);
                    Join<Course, Expert> joinExpert = joinCourse.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getExpertId()));
                }
                query.orderBy(cb.asc(root.get("ordering")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
