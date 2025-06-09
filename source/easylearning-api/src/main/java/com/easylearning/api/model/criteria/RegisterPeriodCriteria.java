package com.easylearning.api.model.criteria;

import com.easylearning.api.model.RegisterPeriod;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RegisterPeriodCriteria {
    private Long id;
    private Integer status;
    private Integer state;
    private String name;
    private Date startDate;
    private Date endDate;
    private Double totalPayout;
    private Boolean isOrderByCreatedDate;

    public Specification<RegisterPeriod> getSpecification() {
        return new Specification<RegisterPeriod>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RegisterPeriod> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getName() != null) {
                    predicates.add(cb.equal(root.get("name"), getName()));
                }

                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }

                if (getStartDate() != null && getEndDate() != null) {
                    predicates.add(cb.between(root.get("createdDate"), getStartDate(), getEndDate()));
                    predicates.add(cb.notEqual(root.get("createdDate"), getEndDate()));
                } else if (getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), getStartDate()));
                } else if (getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), getEndDate()));
                }

                if (getTotalPayout() != null) {
                    predicates.add(cb.equal(root.get("totalPayout"), getTotalPayout()));
                }
                if(Boolean.TRUE.equals(isOrderByCreatedDate)){
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
