package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatisticalCriteria {
    private Long id;
    private String statisticalKey;
    private String status;

    public Specification<Statistical> getSpecification() {
        return new Specification<Statistical>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Statistical> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (!StringUtils.isEmpty(getStatisticalKey())) {
                    predicates.add(cb.like(cb.lower(root.get("statisticalKey")), "%" + getStatisticalKey().toLowerCase() + "%"));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
