package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseTransaction;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.Version;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class VersionCriteria {
    private Long id;
    private Integer status;
    private Integer versionCode;
    private Integer state;
    private String reviewNote;
    private Long courseId;
    private Long expertId;
    public Specification<Version> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getVersionCode() != null) {
                    predicates.add(cb.equal(root.get("versionCode"), getVersionCode()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if (!StringUtils.isBlank(getReviewNote())) {
                    predicates.add(cb.like(cb.lower(root.get("reviewNote")), "%" + getReviewNote() + "%"));
                }
                if (getCourseId() != null) {
                    predicates.add(cb.equal(root.get("courseId"), getCourseId()));
                }
                if(getExpertId()!=null){
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Course> subRootCourse = subquery.from(Course.class);
                    subquery.select(subRootCourse.get("id"));
                    subquery.where(cb.equal(subRootCourse.get("expert").get("id"), getExpertId()));

                    query.where(root.get("courseId").in(subquery));
                    predicates.add(cb.in(root.get("courseId")).value(subquery));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
