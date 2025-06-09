package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseReviewHistoryCriteria {
    private Long id;
    private Integer status;
    private Long versionId;
    private Long date;
    private Integer state;
    private Integer versionState;

    public Specification<CourseReviewHistory> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CourseReviewHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if (getVersionId() != null) {
                    Join<Version, CourseReviewHistory> joinSeller = root.join("version", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("id"), getVersionId()));
                }
                if (getVersionState() != null) {
                    Join<Version, CourseReviewHistory> joinSeller = root.join("version", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("state"), getVersionState()));
                }
                if (getDate() != null) {
                    predicates.add(cb.equal(root.get("date"), getDate()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
