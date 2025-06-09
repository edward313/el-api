package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RegistrationCriteria {
    private Long id;
    private Integer status;
    private Long studentId;
    private Long courseId;
    private Boolean isFinished;
    private Long expertId;
    private Boolean isOrderByCreatedDate;
    private String studentName;
    private String phone;
    private String email;

    public Specification<Registration> getSpecification() {
        return new Specification<Registration>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Registration> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getId()));
                }
                if (getCourseId() != null) {
                    Join<Registration, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));
                }
                if (getExpertId() != null) {
                    Join<Registration, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("expert").get("id"), getExpertId()));
                }
                if (getStudentId() != null) {
                    Join<Registration, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("id"), getStudentId()));
                }
                if (getIsFinished() != null) {
                    predicates.add(cb.equal(root.get("isFinished"), getIsFinished()));
                }
                if(!StringUtils.isEmpty(getEmail())){
                    Join<Registration, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("account").get("email"), getEmail()));
                }
                if(!StringUtils.isEmpty(getStudentName())){
                    Join<Registration, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinStudent.get("account").get("fullName")), "%"+getStudentName().toLowerCase()+"%"));
                }
                if(!StringUtils.isEmpty(getPhone())){
                    Join<Registration, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("account").get("phone"), getPhone()));
                }
                if(Boolean.TRUE.equals(isOrderByCreatedDate)) {
                    query.orderBy(cb.desc(root.get("createdDate")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
