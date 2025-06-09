package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class StudentCriteria {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private Integer status;
    private String address;
    private Boolean isSeller;
    private Long wardId;
    private Long districtId;
    private Long provinceId;
    private String referralCode;
    private Long expertId;
    private Long referralSellerId;
    private Boolean isSystemSeller;
    public Specification<Student> getSpecification() {
        return new Specification<Student>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId()!=null)
                {
                    predicates.add(cb.equal(root.get("id"),getId()));
                }
                if(getStatus()!=null)
                {
                    predicates.add(cb.equal(root.get("status"),getStatus()));
                }
                if(getIsSeller()!=null)
                {
                    predicates.add(cb.equal(root.get("isSeller"),getIsSeller()));
                }
                if(getIsSystemSeller()!=null){
                    predicates.add(cb.equal(root.get("isSystemSeller"),getIsSystemSeller()));
                }
                if(getReferralCode()!=null)
                {
                    predicates.add(cb.equal(root.get("referralCode"),getReferralCode()));
                }
                if(getDistrictId() != null){
                    Join<Nation, Student> join = root.join("district", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getDistrictId()));
                }
                if(getWardId() != null){
                    Join<Nation, Student> join = root.join("ward", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getWardId()));
                }
                if(getProvinceId() != null){
                    Join<Nation, Student> join = root.join("province", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getProvinceId()));
                }
                if (getExpertId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<CourseRetail> subqueryRoot = subquery.from(CourseRetail.class);
                    subquery.select(subqueryRoot.get("seller").get("id"));
                    subquery.where(cb.equal(subqueryRoot.get("course").get("expert").get("id"), getExpertId()));

                    predicates.add(root.get("id").in(subquery));
                }
                if(!StringUtils.isBlank(getAddress())){
                    predicates.add(cb.equal(cb.lower(root.get("address")),"%"+ getAddress()+"%"));
                }
                if (!StringUtils.isBlank(getPhone()))
                {
                    Join<Student, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("phone")),"%"+ getPhone()+"%"));
                }
                if (!StringUtils.isBlank(getEmail()))
                {
                    Join<Student, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("email")),"%"+ getEmail()+"%"));
                }
                if (!StringUtils.isBlank(getFullName()))
                {
                    Join<Student, Account> joinAccount = root.join("account",JoinType.INNER);
                    predicates.add(cb.like(cb.lower(joinAccount.get("fullName")),"%"+ getFullName()+"%"));
                }
                if (getReferralSellerId() != null) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<ReferralSellerLog> subqueryRoot = subquery.from(ReferralSellerLog.class);
                    subquery.select(subqueryRoot.get("student").get("id"));
                    subquery.where(cb.equal(subqueryRoot.get("refStudent").get("id"), getReferralSellerId()));

                    predicates.add(root.get("id").in(subquery));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
