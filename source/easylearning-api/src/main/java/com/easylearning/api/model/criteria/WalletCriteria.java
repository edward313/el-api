package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Account;
import com.easylearning.api.model.Wallet;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
@Data
public class WalletCriteria {
    private Long id;
    private Long accountId;
    private Integer kind;
    private String walletNumber;
    private Integer status;

    public Specification<Wallet> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Wallet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if (getWalletNumber() != null) {
                    predicates.add(cb.equal(root.get("walletNumber"), getWalletNumber()));
                }
                if (getAccountId() != null) {
                    Join<Account, Wallet> join = root.join("account", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getAccountId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
