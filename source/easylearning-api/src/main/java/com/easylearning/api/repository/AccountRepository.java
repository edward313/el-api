package com.easylearning.api.repository;

import com.easylearning.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    Account findAccountByEmailOrGoogleId(String email, String googleId);
    Account findAccountByPhoneAndKind(String phone, Integer kind);
    @Query(value = "SELECT * FROM db_el_account WHERE (phone = :phone OR email = :email) AND kind = :kind", nativeQuery = true)
    Account findByPhoneOrEmailAndKind(String phone, String email, Integer kind);
    Account findFirstByFacebookId(String facebookId);
    Account findAccountByEmailAndKind(String email, Integer kind);

    @Query("SELECT a " +
            "FROM Account a " +
            "INNER JOIN Student s ON s.id = a.id " +
            "WHERE a.email =:email AND a.kind =:kind AND s.isSeller =:isSeller")
    Account findByEmailAndKindAndIsSeller(@Param("email") String email, @Param("kind") Integer kind, @Param("isSeller") Boolean isSeller);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.avatarPath = :avatarPath WHERE a.id = :accountId")
    void updateAvatarByAccountId(@Param("avatarPath") String avatarPath, @Param("accountId") Long accountId);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN Wallet w ON a.id = w.account.id " +
            "LEFT JOIN Expert e ON a.id = e.account.id " +
            "LEFT JOIN Student s ON a.id = s.account.id " +
            "WHERE (e.account.id IS NOT NULL OR (s.account.id IS NOT NULL)) " +
            "AND w.account.id IS NULL")
    List<Account> findAllUserWithOutWallet();

    List<Account> findAllByKindInAndIdNotIn(List<Integer> kinds, List<Long> ids);

    @Query("SELECT a " +
            "FROM Account a " +
            "LEFT JOIN Student s ON s.account.id = a.id " +
            "WHERE ((a.kind =:kindSeller AND s.isSeller IS TRUE) OR a.kind =:kindExpert) AND a.id NOT IN :ids")
    List<Account> findAllByKindSellerAndKindExpertAndIdNotIn(@Param("kindSeller") Integer kindSeller, @Param("kindExpert") Integer kindExpert, @Param("ids") List<Long> ids);
}
