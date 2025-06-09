package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseRetail;
import com.easylearning.api.model.SellerCodeTracking;
import com.easylearning.api.model.Student;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class SellerCodeTrackingCriteria {
    private Long id;
    private Integer status;
    private Long studentId;
    private String sellCode;
    private String browserCode;

    public Specification<SellerCodeTracking> getSpecification() {
        return new Specification<SellerCodeTracking>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<SellerCodeTracking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getStudentId() != null) {
                    Join<SellerCodeTracking, Student> studentJoin = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(studentJoin.get("id"), getStudentId()));
                }
                if (!StringUtils.isEmpty(getSellCode())) {
                    predicates.add(cb.equal(cb.lower(root.get("sellCode")), getSellCode().toLowerCase()));
                }
                if (!StringUtils.isEmpty(getBrowserCode())) {
                    predicates.add(cb.equal(cb.lower(root.get("browserCode")), getBrowserCode().toLowerCase()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
