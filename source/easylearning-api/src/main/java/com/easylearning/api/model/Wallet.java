package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_wallet")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Wallet extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    // 0: seller, 1: expert, 2: student
    private Integer kind;
    @Column(name = "holding_balance")
    private Double holdingBalance = 0.0;
    private Double balance = 0.0;
    @Column(name = "wallet_number")
    private String walletNumber;
}
