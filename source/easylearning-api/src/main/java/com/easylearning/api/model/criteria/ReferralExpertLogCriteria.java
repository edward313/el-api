package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ReferralExpertLog;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Data
public class ReferralExpertLogCriteria {
    private Long id;
    private Long expertId;
    private Long refExpertId;

    public Specification<ReferralExpertLog> getSpecification() {
        return new Specification<ReferralExpertLog>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ReferralExpertLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId() != null){
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getExpertId() != null) {
                    Join<Expert, ReferralExpertLog> joinRoot = root.join("expert", JoinType.INNER);
                    predicates.add(cb.equal(joinRoot.get("id"), getExpertId()));
                }
                if (getRefExpertId() != null) {
                    Join<Expert, ReferralExpertLog> joinRoot = root.join("refExpert", JoinType.INNER);
                    predicates.add(cb.equal(joinRoot.get("id"), getRefExpertId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
