package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseTransactionCriteria {
    private Long id;
    private Integer status;
    private Long expertId;
    private Long sellerId;
    private Long courseId;
    private Long bookingId;
    private Double price;
    private String refSellCode;

    public Specification<CourseTransaction> getSpecification() {
        return new Specification<CourseTransaction>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CourseTransaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if (getExpertId() != null) {
                    Join<CourseTransaction, Expert> joinExpert = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getExpertId()));
                }
                if (getBookingId() != null) {
                    Join<CourseTransaction, Booking> joinExpert = root.join("booking", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getBookingId()));
                }

                if (getSellerId() != null) {
                    Join<CourseTransaction, Student> joinSeller = root.join("seller", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("id"), getSellerId()));
                }

                if (getCourseId() != null) {
                    Join<CourseTransaction, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));
                }

                if (getPrice() != null) {
                    predicates.add(cb.equal(root.get("price"), getPrice()));
                }

                if (!StringUtils.isBlank(getRefSellCode())) {
                    predicates.add(cb.equal(root.get("refSellCode"), getRefSellCode()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
