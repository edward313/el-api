package com.easylearning.api.model.criteria;

import com.easylearning.api.controller.ABasicController;
import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RegisterPayoutCriteria {
    private Long id;
    private Long accountId;
    // 0: seller, 1: expert
    private Integer accountKind;
    // 1:individual , 2: sum
    private Integer kind;
    // 0: pending, 1: calculated , 2: approved, 3: cancelled
    private Integer state;
    private Integer status;
    private Boolean isOrderByCreatedDate;
    private String phone;
    private String fullName;
    private Boolean isNextPeriod;
    private Boolean isIgnoreSystemSeller;
    private Boolean isIgnoreSystemExpert;
    private List<Integer> ignoreStates;
    private Boolean orderByAccountName;
    private Long registerPeriodId;

    public Specification<RegisterPayout> getSpecification(){
        return new Specification<RegisterPayout>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RegisterPayout> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getRegisterPeriodId() != null) {
                    predicates.add(cb.equal(root.get("registerPeriod").get("id"), getRegisterPeriodId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getId()));
                }
                if (getAccountId() != null) {
                    Join<MonthlyPeriodDetail, Account> joinAccount = root.join("account", JoinType.INNER);
                    predicates.add(cb.equal(joinAccount.get("id"), getAccountId()));
                }
                if (StringUtils.isNoneBlank(getFullName())) {
                    Join<MonthlyPeriodDetail, Account> joinAccount = root.join("account", JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("fullName")), "%"+getFullName().toLowerCase()+"%"));
                }
                if (StringUtils.isNoneBlank(getPhone())) {
                    Join<MonthlyPeriodDetail, Account> joinAccount = root.join("account", JoinType.INNER);
                    predicates.add(cb.equal(joinAccount.get("phone"), getPhone()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getAccountKind() != null) {
                    predicates.add(cb.equal(root.get("accountKind"), getAccountKind()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if(Boolean.TRUE.equals(getIsNextPeriod())){
                    ABasicController aBasicController = new ABasicController();
                    Date currentDate = new Date();
                    List<Date> dateRange = aBasicController.getDateRange(currentDate);
                    predicates.add(cb.between(root.get("createdDate"), dateRange.get(0), dateRange.get(1)));
                    predicates.add(cb.notEqual(root.get("createdDate"),dateRange.get(1)));
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
                if (getIgnoreStates() != null && !getIgnoreStates().isEmpty()) {
                    predicates.add(cb.not(root.get("state").in(getIgnoreStates())));
                }
                if(Boolean.TRUE.equals(getOrderByAccountName())){
                    query.orderBy(cb.asc(root.get("account").get("fullName")));
                }
                List<Order> orders = new ArrayList<>();
                if(Boolean.TRUE.equals(isOrderByCreatedDate)){
                    orders.add(cb.desc(root.get("createdDate")));
                }
                if (!orders.isEmpty()) {
                    query.orderBy(orders);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
