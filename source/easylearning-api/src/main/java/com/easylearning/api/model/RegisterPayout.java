package com.easylearning.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_register_payout")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RegisterPayout extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "register_period_id")
    private RegisterPeriod registerPeriod;

    // 4: seller, 5: expert
    private Integer accountKind;

    private Double money;

    private Integer state; // 0: pending,1: calculated, 2: approved, 3: cancelled 4: admin cancel

    private Integer kind; // 1:individual , 2: sum

    @Column(columnDefinition = "TEXT")
    private String note;

    private String bankInfo;

    private Integer taxPercent = 0;

    @Column(name = "tax_money")
    private Double taxMoney = 0D;
}
