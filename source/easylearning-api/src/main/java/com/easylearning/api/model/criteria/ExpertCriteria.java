package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
@Data
public class ExpertCriteria {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private Integer status;
    private String referralCode;
    private Long referralExpertId;
    private Long wardId;
    private Long provinceId;
    private Long districtId;
    private String address;
    private Boolean isSystemExpert;
    private Integer ordering;
    private Boolean isOutstanding;
    public Specification<Expert> getSpecification() {
        return new Specification<Expert>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Expert> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId()!=null)
                {
                    predicates.add(cb.equal(root.get("id"),getId()));
                }
                if(getIsOutstanding()!=null)
                {
                    predicates.add(cb.equal(root.get("isOutstanding"),getIsOutstanding()));
                }
                if(getOrdering()!=null)
                {
                    predicates.add(cb.equal(root.get("ordering"),getOrdering()));
                }
                if(getStatus()!=null)
                {
                    predicates.add(cb.equal(root.get("status"),getStatus()));
                }
                if (!StringUtils.isBlank(getPhone()))
                {
                    Join<Expert, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("phone")),"%"+ getPhone()+"%"));
                }
                if (!StringUtils.isBlank(getEmail()))
                {
                    Join<Expert, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("email")),"%"+ getEmail()+"%"));
                }
                if (!StringUtils.isBlank(getFullName()))
                {
                    Join<Expert, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("fullName")),"%"+ getFullName()+"%"));
                }
                if (!StringUtils.isBlank(getReferralCode()))
                {
                    predicates.add(cb.equal(root.get("referralCode"),getReferralCode()));
                }
                if (getReferralExpertId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<ReferralExpertLog> subqueryRoot = subquery.from(ReferralExpertLog.class);
                    subquery.select(subqueryRoot.get("expert").get("id"));
                    subquery.where(cb.equal(subqueryRoot.get("refExpert").get("id"), getReferralExpertId()));

                    predicates.add(root.get("id").in(subquery));
                }
                if(getWardId() != null){
                    Join<Nation, Expert> join = root.join("ward", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getWardId()));
                }
                if(getDistrictId() != null){
                    Join<Nation, Expert> join = root.join("district", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getDistrictId()));
                }
                if(getProvinceId() != null){
                    Join<Nation, Expert> join = root.join("province", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getProvinceId()));
                }
                if(StringUtils.isNoneBlank(getAddress())){
                    predicates.add(cb.like(cb.lower(root.get("address")), "%" + getAddress().toLowerCase() + "%"));
                }
                if (getIsSystemExpert() != null) {
                    predicates.add(cb.equal(root.get("isSystemExpert"), getIsSystemExpert()));
                }
                if(getIsOutstanding() != null && getIsOutstanding())
                {
                    query.orderBy(cb.asc(root.get("ordering")));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
