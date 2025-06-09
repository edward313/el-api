package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExpertRegistrationCriteria {
    private Long id;
    private String phone;
    private String email;
    private Integer status;
    private String address;
    private Long wardId;
    private Long districtId;
    private Long categoryId;
    private Long provinceId;
    private String referralCode;
    private String introduction;
    public Specification<ExpertRegistration> getSpecification() {
        return new Specification<ExpertRegistration>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ExpertRegistration> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId()!=null)
                {
                    predicates.add(cb.equal(root.get("id"),getId()));
                }
                if(getStatus()!=null)
                {
                    predicates.add(cb.equal(root.get("status"),getStatus()));
                }
                if(getReferralCode()!=null)
                {
                    predicates.add(cb.equal(root.get("referralCode"),getReferralCode()));
                }
                if(getDistrictId() != null){
                    Join<Nation, ExpertRegistration> join = root.join("district", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getDistrictId()));
                }
                if(getWardId() != null){
                    Join<Nation, ExpertRegistration> join = root.join("ward", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getWardId()));
                }
                if(getCategoryId() != null){
                    Join<Category, ExpertRegistration> join = root.join("field", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getCategoryId()));
                }
                if(getProvinceId() != null){
                    Join<Nation, ExpertRegistration> join = root.join("province", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getProvinceId()));
                }
                if(StringUtils.isNoneBlank(getIntroduction())){
                    predicates.add(cb.like(cb.lower(root.get("introduction")), "%" + getIntroduction().toLowerCase() + "%"));
                }
                if(StringUtils.isNoneBlank(getAddress())){
                    predicates.add(cb.like(cb.lower(root.get("address")), "%" + getAddress().toLowerCase() + "%"));
                }
                if (StringUtils.isNoneBlank(getPhone()))
                {
                    predicates.add(cb.equal(cb.lower(root.get("phone")),getPhone()));
                }
                if (StringUtils.isNoneBlank(getEmail()))
                {
                    predicates.add(cb.equal(cb.lower(root.get("email")),getEmail()));
                }
                query.orderBy(cb.desc(root.get("createdDate")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
