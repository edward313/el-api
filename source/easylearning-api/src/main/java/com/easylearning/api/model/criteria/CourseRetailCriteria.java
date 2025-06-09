package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseRetail;
import com.easylearning.api.model.Student;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseRetailCriteria {
    private Long id;
    private Integer status;
    private Long sellerId;
    private Long courseId;
    private String sellCode;

    public Specification<CourseRetail> getSpecification() {
        return new Specification<CourseRetail>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CourseRetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if (getSellerId() != null) {
                    Join<CourseRetail, Student> joinSeller = root.join("seller", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("id"), getSellerId()));
                }

                if (getCourseId() != null) {
                    Join<CourseRetail, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));
                }

                if (!StringUtils.isBlank(getSellCode())) {
                    predicates.add(cb.equal(root.get("sellCode"), getSellCode()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
