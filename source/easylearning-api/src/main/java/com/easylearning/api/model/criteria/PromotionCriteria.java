package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Account;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.Notification;
import com.easylearning.api.model.Promotion;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PromotionCriteria {
    private Long id;
    private String name;
    private String description;
    private Integer kind;
    private Integer status;
    private Integer type;
    private Double discountValue;
    private Double limitValue;
    private Double quantity;
    private Integer state;

    public Specification<Promotion> getCriteria() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Promotion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getType() != null) {
                    predicates.add(cb.equal(root.get("type"), getType()));
                }
                if (getDiscountValue() != null) {
                    predicates.add(cb.equal(root.get("discountValue"), getDiscountValue()));
                }
                if (getLimitValue() != null) {
                    predicates.add(cb.equal(root.get("limitValue"), getLimitValue()));
                }
                if (getQuantity() != null) {
                    predicates.add(cb.equal(root.get("quantity"), getQuantity()));
                }
                if (!StringUtils.isBlank(getName()))
                {
                    predicates.add(cb.like(cb.lower(root.get("name")),"%"+ getName()+"%"));
                }
                if (!StringUtils.isBlank(getDescription()))
                {
                    predicates.add(cb.like(cb.lower(root.get("description")),"%"+ getDescription()+"%"));
                }
                query.orderBy(cb.desc(root.get("createdDate")));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
