package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Category;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.Nation;
import com.easylearning.api.model.News;
import com.easylearning.api.repository.NewsRepository;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewsCriteria {
    private Long id;
    private Long categoryId;
    private Integer kind; // 1 introduction
    private String content;
    private String title;
    private String description;
    private String mobileBanner;
    private String banner;
    private String avatar;
    private Integer status;

    public Specification<News> getSpecification() {
        return new Specification<News>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (!StringUtils.isEmpty(getDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + getDescription().toLowerCase() + "%"));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getCategoryId() != null) {
                    Join<Category, News> joinCategory = root.join("category", JoinType.INNER);
                    predicates.add(cb.equal(joinCategory.get("id"), getCategoryId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getTitle() != null) {
                    predicates.add(cb.like(root.get("title"), "%" + getTitle().toLowerCase() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
