package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_monthly_period_detail")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MonthlyPeriodDetail extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // 0: seller, 1: expert
    private Integer kind;

    @Column(name = "total_money")
    private Double totalMoney = 0D;

    @Column(name = "total_ref_money")
    private Double totalRefMoney = 0D;

    @ManyToOne
    @JoinColumn(name = "monthly_period_id")
    private MonthlyPeriod period;

    // 0: unpaid, 1: paid
    private Integer state;
}
