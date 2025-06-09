package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingCriteria {
    private Long id;
    private Integer status;
    private Integer state;
    private Long studentId;
    private Double totalMoney;
    private Double saleOffMoney;
    private Integer paymentMethod;
    private Integer payoutStatus;
    private String code;
    private Integer ignoreState;

    public Specification<Booking> getSpecification() {
        return new Specification<Booking>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getCode() != null) {
                    predicates.add(cb.equal(root.get("code"), getCode()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }

                if (getStudentId() != null) {
                    Join<Booking, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("id"), getStudentId()));
                }

                if (getTotalMoney() != null) {
                    predicates.add(cb.equal(root.get("totalMoney"), getTotalMoney()));
                }

                if (getSaleOffMoney() != null) {
                    predicates.add(cb.equal(root.get("saleOffMoney"), getSaleOffMoney()));
                }

                if (getPaymentMethod() != null) {
                    predicates.add(cb.equal(root.get("paymentMethod"), getPaymentMethod()));
                }

                if (getPayoutStatus() != null) {
                    predicates.add(cb.equal(root.get("payoutStatus"), getPayoutStatus()));
                }
                if (getIgnoreState() != null) {
                    predicates.add(cb.notEqual(root.get("state"), getIgnoreState()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
