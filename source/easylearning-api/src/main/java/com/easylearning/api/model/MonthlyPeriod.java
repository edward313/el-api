package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_monthly_period")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MonthlyPeriod extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "system_revenue")
    private Double systemRevenue;

    @Column(name = "total_payout")
    private Double totalPayout;

    @Column(name = "total_sale_off_money")
    private Double totalSaleOffMoney = 0D;

    // 0: pending, 1: calculated, 2: done
    private Integer state;

}
