package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Data
public class ReviewCriteria {

    private Long id;
    private Integer star;
    private Long studentId;
    private Long courseId;
    private Integer status;
    private String message;
    private Long expertId;
    private Integer kind;
    private String fullName;
    public Specification<Review> getCriteria() {
        return new Specification<Review>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Review> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(getId() != null){
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if(getStudentId() != null){
                    Join<Review, Student> joinGroup = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinGroup.get("id"), getStudentId()));
                }
                if(getFullName() != null){
                    Join<Review, Student> joinStudent = root.join("student", JoinType.INNER);
                    Join<Student, Account> joinAccount = joinStudent.join("account", JoinType.INNER);
                    predicates.add(cb.like(joinAccount.get("fullName"), "%" + getFullName().toLowerCase() + "%"));
                }
                if(getCourseId() != null){
                    Join<Review, Course> joinGroup = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinGroup.get("id"), getCourseId()));
                }
                if(getStatus() != null){
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if(getKind() != null){
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if(getStar() != null){
                    predicates.add(cb.equal(root.get("star"), getStar()));
                }

                if (!StringUtils.isEmpty(getMessage())) {
                    predicates.add(cb.like(cb.lower(root.get("message")), "%" + getMessage().toLowerCase() + "%"));
                }
                if (getExpertId() != null) {
                    Join<Review, Expert> joinExpert = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getExpertId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
