package com.easylearning.api.model.criteria;

import com.easylearning.api.model.SlideShow;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class SlideShowCriteria {
    private Long id;
    private String image;
    private String url;
    private Integer status;
    private Integer action;
    private Integer ordering;
    private String title;
    private String description;
    public Specification<SlideShow> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<SlideShow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getAction() != null) {
                    predicates.add(cb.equal(root.get("action"), getAction()));
                }
                if (!StringUtils.isEmpty(getImage())) {
                    predicates.add(cb.like(cb.lower(root.get("image")), "%" + getImage().toLowerCase() + "%"));
                }
                if (!StringUtils.isEmpty(getUrl())) {
                    predicates.add(cb.like(cb.lower(root.get("url")), "%" + getUrl().toLowerCase() + "%"));
                }
                if (getOrdering() != null) {
                    predicates.add(cb.equal(root.get("ordering"), getOrdering()));
                }
                if (!StringUtils.isEmpty(getDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + getDescription().toLowerCase() + "%"));
                }
                if (!StringUtils.isEmpty(getTitle())) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + getTitle().toLowerCase() + "%"));
                }
                query.orderBy(cb.asc(root.get("ordering")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
