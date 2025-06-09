package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
@Data
public class PromotionCodeCriteria {
    private Long id;
    private String code;
    private Long promotionId;
    private Integer status;
    private Integer quantityUsed;

    public Specification<PromotionCode> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PromotionCode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getCode() != null) {
                    predicates.add(cb.equal(root.get("code"), getCode()));
                }
                if (getQuantityUsed() != null) {
                    predicates.add(cb.equal(root.get("quantityUsed"), getQuantityUsed()));
                }
                if (getPromotionId() != null) {
                    Join<PromotionCode, Promotion> joinExpert = root.join("promotion", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getPromotionId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
