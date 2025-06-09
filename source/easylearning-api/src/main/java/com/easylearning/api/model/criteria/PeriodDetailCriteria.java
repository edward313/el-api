package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PeriodDetailCriteria {
    private Long id;
    private Integer status;
    private Long accountId;
    private Integer kind;
    private Integer state;
    private Long periodId;
    private Double totalMoney;
    private Integer totalRefCourse;
    private Integer totalCourse;
    private Boolean orderByAccountName;
    private Boolean isIgnoreSystemSeller;
    private Boolean isIgnoreSystemExpert;
    private List<Integer> ignoreStates;
    private Boolean isOrderByCreatedDate;


    public Specification<MonthlyPeriodDetail> getSpecification() {
        return new Specification<MonthlyPeriodDetail>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<MonthlyPeriodDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();


                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getAccountId() != null) {
                    Join<MonthlyPeriodDetail, Account> joinAccount = root.join("account", JoinType.INNER);
                    predicates.add(cb.equal(joinAccount.get("id"), getAccountId()));
                }

                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }

                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if (getIgnoreStates() != null && !getIgnoreStates().isEmpty()) {
                    predicates.add(cb.not(root.get("state").in(getIgnoreStates())));
                }
                if (getPeriodId() != null) {
                    Join<MonthlyPeriodDetail, MonthlyPeriod> joinPeriod = root.join("period", JoinType.INNER);
                    predicates.add(cb.equal(joinPeriod.get("id"), getPeriodId()));
                }

                if (getTotalMoney() != null) {
                    predicates.add(cb.equal(root.get("totalMoney"), getTotalMoney()));
                }

                if (getTotalRefCourse() != null) {
                    predicates.add(cb.equal(root.get("totalRefCourse"), getTotalRefCourse()));
                }

                if (getTotalCourse() != null) {
                    predicates.add(cb.equal(root.get("totalCourse"), getTotalCourse()));
                }


                if(Boolean.TRUE.equals(getOrderByAccountName())){
                    query.orderBy(cb.asc(root.get("account").get("fullName")));
                }
                if(Boolean.TRUE.equals(isIgnoreSystemSeller)){
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Student> subRoot = subquery.from(Student.class);
                    subquery.select(subRoot.get("account").get("id"));
                    Predicate predicate = cb.equal(subRoot.get("isSystemSeller"), true);
                    subquery.where(cb.and(predicate));
                    predicates.add(root.get("account").get("id").in(subquery).not());
                }
                if(Boolean.TRUE.equals(isIgnoreSystemExpert)){
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Expert> subRoot = subquery.from(Expert.class);
                    subquery.select(subRoot.get("account").get("id"));
                    Predicate predicate = cb.equal(subRoot.get("isSystemExpert"), true);
                    subquery.where(cb.and(predicate));
                    predicates.add(root.get("account").get("id").in(subquery).not());
                }
                if(Boolean.TRUE.equals(isOrderByCreatedDate)){
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
