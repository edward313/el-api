package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Category;
import com.easylearning.api.model.CategoryHome;
import com.easylearning.api.model.Course;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryHomeCriteria {
    private Long id;
    private Integer status;
    private Long categoryId;
    private Long courseId;

    public Specification<CategoryHome> getSpecification() {
        return new Specification<CategoryHome>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CategoryHome> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getCategoryId() != null) {
                    Join<Category, CategoryHome> joinField = root.join("category", JoinType.INNER);
                    predicates.add(cb.equal(joinField.get("id"), getCategoryId()));
                }
                if (getCourseId() != null) {
                    Join<Course, CategoryHome> joinExpert = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinExpert.get("id"), getCourseId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
