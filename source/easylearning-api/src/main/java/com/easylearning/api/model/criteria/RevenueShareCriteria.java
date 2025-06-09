package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
public class RevenueShareCriteria {
    private Long id;
    private Integer status;
    private Long sellerId;
    private Long expertId;
    private Long courseTransactionId;
    private Boolean isRef;
    private Boolean isSystem;
    private Integer ratioShare;
    private Date startDate;
    private Date endDate;
    private Integer kind;
    private Long periodId;
    private Long accountId;
    private Integer ignoreKind;
    private Boolean isOrderByCreatedDate;
    private Boolean getLastThreeMonths;
    private Integer payoutStatus;
    private String bookingCode;


    public Specification<RevenueShare> getSpecification() {
        return new Specification<RevenueShare>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RevenueShare> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getIgnoreKind() != null) {
                    predicates.add(cb.notEqual(root.get("kind"), getIgnoreKind()));
                }
                if (getSellerId() != null) {
                    Join<RevenueShare, Student> joinSeller = root.join("seller", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("id"), getSellerId()));
                }
                if (getAccountId() != null) {
                    Predicate predicate = null;
                    if (root.get("seller") != null) {
                        Join<RevenueShare, Student> joinSeller = root.join("seller", JoinType.LEFT);
                        predicate = cb.equal(joinSeller.get("id"), getAccountId());
                    }
                    if (root.get("expert") != null) {
                        Join<RevenueShare, Expert> joinExpert = root.join("expert", JoinType.LEFT);
                        predicate = cb.equal(joinExpert.get("id"), getAccountId());
                    }
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
                if (getExpertId() != null) {
                    Join<RevenueShare, Expert> joinStudent = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("id"), getExpertId()));
                }
                if (getPeriodId() != null) {
                    predicates.add(cb.equal(root.get("payoutPeriod").get("id"), getPeriodId()));
                }
                if (getCourseTransactionId() != null) {
                    Join<RevenueShare, CourseTransaction> joinStudent = root.join("courseTransaction", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("id"), getCourseTransactionId()));
                }
                if (getStartDate() != null && getEndDate() != null) {
                    predicates.add(cb.between(root.get("createdDate"), getStartDate(), getEndDate()));
                    predicates.add(cb.notEqual(root.get("createdDate"), getEndDate()));
                } else if (getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), getStartDate()));
                } else if (getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), getEndDate()));
                }
                if (getIsRef() != null) {
                    if (getIsRef()) {
                        predicates.add(cb.isTrue(root.get("isRef")));
                    } else {
                        predicates.add(cb.or(
                                cb.isFalse(root.get("isRef")),
                                cb.isNull(root.get("isRef"))
                        ));
                    }
                }
                if (getIsSystem() != null) {
                    if (getIsSystem()) {
                        predicates.add(cb.isTrue(root.get("isSystem")));
                    } else {
                        predicates.add(cb.or(
                                cb.isFalse(root.get("isSystem")),
                                cb.isNull(root.get("isSystem"))
                        ));
                    }
                }
                if (getRatioShare() != null) {
                    predicates.add(cb.equal(root.get("ratioShare"), getRatioShare()));
                }
                if(Boolean.TRUE.equals(isOrderByCreatedDate)){
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                if (Boolean.TRUE.equals(getLastThreeMonths)) {
                    Date currentDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.MONTH, -3);

                    Date threeMonthsAgo = calendar.getTime();
                    predicates.add(cb.between(root.get("createdDate"), threeMonthsAgo, currentDate));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getPayoutStatus() != null) {
                    predicates.add(cb.equal(root.get("payoutStatus"), getPayoutStatus()));
                }
                if (getBookingCode() != null) {
                    predicates.add(cb.equal(root.get("courseTransaction").get("booking").get("code"), getBookingCode()));
                }

                Join<RevenueShare, CourseTransaction> joinCourseTransaction = root.join("courseTransaction", JoinType.INNER);
                Join<CourseTransaction, Booking> joinBooking = joinCourseTransaction.join("booking", JoinType.INNER);
                Join<CourseTransaction, Course> joinCourse = joinCourseTransaction.join("course", JoinType.INNER);

                query.orderBy(
                        cb.desc(root.get("createdDate")),
                        cb.asc(joinBooking.get("id")),
                        cb.asc(joinCourse.get("id"))
                );
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
