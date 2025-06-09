package com.easylearning.api.model.criteria;

import com.easylearning.api.model.ReferralSellerLog;
import com.easylearning.api.model.Student;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ReferralSellerLogCriteria {
    private Long id;
    private Date usedTime;
    private Long studentId;
    private Long sellerId;
    public Specification<ReferralSellerLog> getSpecification() {
        return new Specification<ReferralSellerLog>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ReferralSellerLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId()!=null)
                {
                    predicates.add(cb.equal(root.get("id"),getId()));
                }
                if(getUsedTime()!=null)
                {
                    predicates.add(cb.equal(root.get("usedTime"),getUsedTime()));
                }
                if(getStudentId() != null){
                    Join<Student, ReferralSellerLog> join = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getStudentId()));
                }
                if(getSellerId() != null){
                    Join<Student, ReferralSellerLog> join = root.join("refStudent", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getSellerId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
