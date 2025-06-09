package com.easylearning.api.repository;

import com.easylearning.api.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long>, JpaSpecificationExecutor<Wallet> {
    Wallet findByWalletNumber(String walletNumber);

    Wallet findFirstByKind(Integer kind);

    Wallet findFirstByAccountId(Long accountId);

    Wallet findByAccountId(Long accountId);
    @Modifying
    @Transactional
    void deleteAllByAccountId(Long accountId);

    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.kind = " +
            "CASE " +
            "WHEN w.account.id IN (SELECT e.account.id FROM Expert e) THEN 1 " +
            "WHEN w.account.id IN (SELECT s.account.id FROM Student s WHERE s.isSeller = true) THEN 0 " +
            "ELSE w.kind " +
            "END " +
            "WHERE w.account.id IN (" +
            "SELECT a.id FROM Account a " +
            "LEFT JOIN Expert e ON a.id = e.account.id " +
            "LEFT JOIN Student s ON a.id = s.account.id " +
            "WHERE e.account.id IS NOT NULL OR (s.account.id IS NOT NULL AND s.isSeller = true))")
    void updateKindForSellersAndExperts();

    Wallet findByAccountIdAndKind(Long accountId, Integer kind);

    @Transactional
    @Modifying
    @Query(value = "UPDATE db_el_wallet SET balance = 0, holding_balance = 0 ", nativeQuery = true)
    void resetBalanceAndHoldingBalance();
}
