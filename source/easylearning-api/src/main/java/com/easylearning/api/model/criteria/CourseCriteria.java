package com.easylearning.api.model.criteria;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseCriteria {
    private Long id;
    private String name;
    private Double price;
    private String query;
    private Integer saleOff;
    private Integer status;
    private Long fieldId;
    private Long sellerId;
    private Long studentId;
    private Long expertId;
    private Long parentId;
    private Integer kind;
    private Integer description;
    private Boolean isSellerCourse;
    private Boolean isFree;
    private Boolean isFinished;
    private List<Long> categoryIds;
    private List<Long> courseDuplicateIds;
    private Long categoryId;
    private Integer searchType;
    private Boolean isActive;
    private Integer ignoreStatus;
    private Integer ignoreKind;
    private Long ignoreId;
    private List<Long> ignoreIds;
    private Boolean orderByCreatedDate;
    private Integer versionState;

    public Specification<Course> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getIgnoreId() != null) {
                    predicates.add(cb.notEqual(root.get("id"), getIgnoreId()));
                }
                if (getIgnoreIds() != null && !getIgnoreIds().isEmpty()) {
                    predicates.add(root.get("id").in(getIgnoreIds()).not());
                }
                if (getParentId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<CourseComboDetail> subRoot = subquery.from(CourseComboDetail.class);
                    subquery.select(subRoot.get("course").get("id"));
                    subquery.where(cb.equal(subRoot.get("combo").get("id"), getParentId()));
                    predicates.add(cb.in(root.get("id")).value(subquery));
                }
                if (getCategoryIds() != null && !getCategoryIds().isEmpty()) {
                    Join<Category, Course> joinCategory = root.join("field", JoinType.INNER);
                    predicates.add(joinCategory.get("id").in(getCategoryIds()));
                }
                if (getCourseDuplicateIds() != null && !getCourseDuplicateIds().isEmpty()) {
                    predicates.add(cb.not(root.get("id").in(getCourseDuplicateIds())));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getIgnoreStatus() != null){
                    predicates.add(cb.notEqual(root.get("status"), getIgnoreStatus()));
                }
                if (getPrice() != null) {
                    predicates.add(cb.equal(root.get("price"), getPrice()));
                }
                if (getIsFree() != null) {
                    if (getIsFree()) {
                        predicates.add(cb.equal(root.get("price"), 0));
                    } else {
                        predicates.add(cb.notEqual(root.get("price"), 0));
                    }
                }
                if (!StringUtils.isBlank(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName() + "%"));
                }
                if (getSaleOff() != null) {
                    predicates.add(cb.equal(root.get("saleOff"), getSaleOff()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getIgnoreKind() != null) {
                    predicates.add(cb.notEqual(root.get("kind"), getIgnoreKind()));
                }
                if (getCategoryId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<CategoryHome> subRoot = subquery.from(CategoryHome.class);
                    Join<CategoryHome, Course> joinCourse = subRoot.join("course", JoinType.INNER);
                    subquery.select(joinCourse.get("id"))
                            .where(cb.equal(subRoot.get("category").get("id"), getCategoryId()));
                    predicates.add(cb.in(root.get("id")).value(subquery));
                }

                if (getFieldId() != null) {
                    Join<Category, Course> joinField = root.join("field", JoinType.INNER);
                    predicates.add(cb.equal(joinField.get("id"), getFieldId()));
                }
                if (getExpertId() != null) {
                    Join<Expert, Course> joinExpert = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getExpertId()));
                }
                if (getSellerId() != null) {
                    Subquery<Long> subQuery = query.subquery(Long.class);
                    Root<CourseRetail> subQueryRoot = subQuery.from(CourseRetail.class);
                    subQuery.select(subQueryRoot.get("course").get("id"));
                    subQuery.where(
                            cb.equal(subQueryRoot.get("seller").get("id"), getSellerId()),
                            cb.equal(subQueryRoot.get("status"), LifeUniConstant.STATUS_ACTIVE));
                    predicates.add(root.get("id").in(subQuery));
                }
                if (getStudentId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Registration> subqueryRoot = subquery.from(Registration.class);
                    subquery.select(subqueryRoot.get("course").get("id"));
                    subquery.where(cb.equal(subqueryRoot.get("student").get("id"), getStudentId()));

                    predicates.add(root.get("id").in(subquery));
                }
                if (getIsSellerCourse() != null) {
                    if (getIsSellerCourse()) {
                        predicates.add(cb.isTrue(root.get("isSellerCourse")));
                    } else {
                        predicates.add(cb.or(
                                cb.isFalse(root.get("isSellerCourse")),
                                cb.isNull(root.get("isSellerCourse"))
                        ));
                    }
                }
                if (getIsFinished() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Registration> subqueryRoot = subquery.from(Registration.class);

                    subquery.select(subqueryRoot.get("course").get("id"));
                    subquery.where(cb.equal(subqueryRoot.get("student").get("id"), getStudentId()),
                            cb.equal(subqueryRoot.get("isFinished"), getIsFinished()));

                    predicates.add(root.get("id").in(subquery));
                }
                if(Boolean.TRUE.equals(orderByCreatedDate)){
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
