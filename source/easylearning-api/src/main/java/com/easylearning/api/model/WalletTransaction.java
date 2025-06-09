package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_wallet_transaction")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class WalletTransaction extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private Double money;
    // 1 in, 2 out
    private Integer kind;
    // 0 pending, 1 fail, 2 success
    private Integer state;
    @Column(name = "note", columnDefinition = "text")
    private String note;
    @Column(name = "last_balance")
    private Double lastBalance;
}