package com.easylearning.api.model.criteria;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.model.Notification;
import com.easylearning.api.validation.AppKind;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class NotificationCriteria {
    private Long id;
    private Long idUser;
    private Integer state;
    private Integer kind;
    private Integer status;
    private String msg;
    @AppKind(allowNull = true)
    private Integer appKind;


    public Specification<Notification> getCriteria() {
        return new Specification<Notification>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(getId() != null){
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if(getIdUser() != null){
                    predicates.add(cb.equal(root.get("idUser"), getIdUser()));
                }
                if(getState() != null){
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if(getKind() != null){
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if(getStatus() != null){
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if(!StringUtils.isBlank(getMsg())){
                    predicates.add(cb.like(cb.lower(root.get("msg")),"%"+ getMsg()+"%"));
                }
                if(getAppKind() != null){
                    if(Objects.equals(getAppKind(), LifeUniConstant.PORTAL_APP)){
                        predicates.add(root.get("kind").in(LifeUniConstant.PORTAL_NOTIFICATION_KINDS));
                    }else if (Objects.equals(getAppKind(), LifeUniConstant.CLIENT_APP)){
                        predicates.add(root.get("kind").in(LifeUniConstant.FE_NOTIFICATION_KINDS));
                    }
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
