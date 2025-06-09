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
public class CourseVersioningCriteria {
    private Long id;
    private String name;
    private Double price;
    private Long expertId;
    private Integer status;
    private List<Integer> maxVersionStates;
    private Boolean orderByCreatedDate;
    private List<Integer> ignoreVisualStatus;
    private List<Integer> ignoreStatus;
    private Boolean isMaxCourseVersioning;
    private Integer kind;
    private Long visualId;
    public Specification<CourseVersioning> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CourseVersioning> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getVisualId() != null) {
                    predicates.add(cb.equal(root.get("visualId"), getVisualId()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getStatus() != null && !getStatus().equals(LifeUniConstant.STATUS_DELETE) && !getStatus().equals(LifeUniConstant.STATUS_LOCK)) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getPrice() != null) {
                    predicates.add(cb.equal(root.get("price"), getPrice()));
                }
                if (!StringUtils.isBlank(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName() + "%"));
                }
                if (getExpertId() != null) {
                    Join<Expert, Course> joinExpert = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getExpertId()));
                }
                if (getIgnoreStatus() != null){
                    predicates.add(root.get("status").in(getIgnoreStatus()).not());
                }
                // filter course versiong mới nhất
                Join<CourseVersioning, Version> versionJoin = root.join("version", JoinType.INNER);
                Subquery<Number> maxVersionSubquery = query.subquery(Number.class);
                Root<CourseVersioning> maxVersionRoot = maxVersionSubquery.from(CourseVersioning.class);
                Join<CourseVersioning, Version> maxVersionJoin = maxVersionRoot.join("version", JoinType.INNER);
                // get max course versioning
                if(Boolean.TRUE.equals(isMaxCourseVersioning)){
                    maxVersionSubquery.select(cb.max(maxVersionJoin.get("versionCode")))
                            .groupBy(maxVersionRoot.get("visualId"))
                            .where(cb.equal(root.get("visualId"), maxVersionRoot.get("visualId")));
                }else{// admin
                    //deleted course
                    if(getStatus()!=null && (getStatus().equals(LifeUniConstant.STATUS_DELETE) || getStatus().equals(LifeUniConstant.STATUS_LOCK))){
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<Course> subRoot = subquery.from(Course.class);
                        subquery.select(cb.max(subRoot.get("id"))).where(cb.equal(subRoot.get("status"), getStatus()),
                                cb.equal(root.get("visualId"),subRoot.get("id")));

                        Subquery<Long> subquery4 = query.subquery(Long.class);
                        Root<Course> subRoot4 = subquery4.from(Course.class);
                        subquery4.select(subRoot4.get("id"));
                        Predicate predicate = cb.equal(subRoot4.get("version").get("versionCode"), LifeUniConstant.VERSION_CODE_INIT);
                        Predicate predicate1 = cb.notEqual(subRoot4.get("version").get("state"), LifeUniConstant.VERSION_STATE_APPROVE);
                        subquery4.where(cb.and(predicate,predicate1));

                        maxVersionSubquery.select(cb.max(maxVersionJoin.get("versionCode")))
                                .groupBy(maxVersionRoot.get("visualId"))
                                .where(cb.equal(root.get("visualId"), maxVersionRoot.get("visualId")),
                                        cb.or(cb.greaterThan(maxVersionJoin.get("state"), 0),root.get("visualId").in(subquery4)),
                                        maxVersionRoot.get("visualId").in(subquery));
                    }else {
                        maxVersionSubquery.select(cb.max(maxVersionJoin.get("versionCode")))
                                .groupBy(maxVersionRoot.get("visualId"))
                                .where(cb.equal(root.get("visualId"), maxVersionRoot.get("visualId")), cb.greaterThan(maxVersionJoin.get("state"), 0));
                    }
                }
                predicates.add(cb.equal(versionJoin.get("versionCode"), maxVersionSubquery));
                //filter theo maxVersionState
                if(getMaxVersionStates() != null && !getMaxVersionStates().isEmpty()){
                    // lấy ra version code cao nhất của mỗi course
                    Subquery<Integer> subquery2 = query.subquery(Integer.class);
                    Root<Version> subRoot2 = subquery2.from(Version.class);
                    subquery2.select(cb.max(subRoot2.get("versionCode")));
                    if(Boolean.TRUE.equals(isMaxCourseVersioning)){ // get max course versioning
                        subquery2.where(cb.equal(root.get("visualId"), subRoot2.get("courseId")));
                    }else {// get course versioning have version state > 0
                        subquery2.where(cb.equal(root.get("visualId"), subRoot2.get("courseId")),cb.greaterThan(subRoot2.get("state"),0));
                    }
                    Expression<Integer> maxVersionCode = cb.any(subquery2);
                    // filter dựa trên course version maxVersionCode và maxVersionState
                    Subquery<Long> subquery3 = query.subquery(Long.class);
                    Root<Version> subRoot3 = subquery3.from(Version.class);
                    subquery3.select(subRoot3.get("courseId"));
                    Predicate predicate1 = cb.equal(root.get("visualId"), subRoot3.get("courseId"));
                    Predicate predicate2 = cb.equal(subRoot3.get("versionCode"), maxVersionCode);
                    Predicate predicate3 = subRoot3.get("state").in(getMaxVersionStates());
                    subquery3.where(cb.and(predicate1, predicate2, predicate3));
                    predicates.add(root.get("visualId").in(subquery3));
                }
                // ignore Course Status
                if (getIgnoreVisualStatus() != null && !getIgnoreVisualStatus().isEmpty()) {
                    Subquery<Long> subquery4 = query.subquery(Long.class);
                    Root<Course> subRoot4 = subquery4.from(Course.class);
                    subquery4.select(subRoot4.get("id"));
                    Predicate predicate = subRoot4.get("status").in(getIgnoreVisualStatus());
                    subquery4.where(cb.and(predicate));
                    predicates.add(root.get("visualId").in(subquery4).not());
                }
                if (Boolean.TRUE.equals(orderByCreatedDate)) {
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
