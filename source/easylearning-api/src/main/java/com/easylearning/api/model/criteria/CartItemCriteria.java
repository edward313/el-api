package com.easylearning.api.model.criteria;

import com.easylearning.api.model.CartItem;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.Student;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CartItemCriteria {
    private Long id;
    private Date timeCreated;
    private Long studentId;
    private Long courseId;

    public Specification<CartItem> getSpecification() {
        return new Specification<CartItem>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CartItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getTimeCreated() != null) {
                    predicates.add(cb.equal(root.get("timeCreated"), getTimeCreated()));
                }
                if (getStudentId() != null) {
                    Join<CartItem, Student> joinSeller = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinSeller.get("id"), getStudentId()));
                }

                if (getCourseId() != null) {
                    Join<CartItem, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));
                }
                query.orderBy(cb.desc(root.get("timeCreated")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
