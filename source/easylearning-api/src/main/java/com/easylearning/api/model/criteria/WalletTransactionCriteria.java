package com.easylearning.api.model.criteria;

import com.easylearning.api.model.Account;
import com.easylearning.api.model.Wallet;
import com.easylearning.api.model.WalletTransaction;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
@Data
public class WalletTransactionCriteria {
    private Long id;
    private Long walletId;
    private Integer kind;
    private Integer state;
    private Integer status;
    private Long accountId;

    public Specification<WalletTransaction> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<WalletTransaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                if (getWalletId() != null) {
                    Join<WalletTransaction, Wallet> join = root.join("wallet", JoinType.INNER);
                    predicates.add(cb.equal(join.get("id"), getWalletId()));
                }
                if (getAccountId() != null) {
                    Join<WalletTransaction, Wallet> walletJoin = root.join("wallet");
                    Join<Wallet, Account> accountJoin = walletJoin.join("account");
                    predicates.add(cb.equal(accountJoin.get("id"), getAccountId()));
                }
                query.orderBy(cb.desc(root.get("createdDate")));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}