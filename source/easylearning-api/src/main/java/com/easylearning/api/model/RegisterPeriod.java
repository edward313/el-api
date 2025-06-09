package com.easylearning.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_register_period")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RegisterPeriod extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    private Date startDate;

    private Date endDate;

    private Double totalPayout;

    private Double totalTaxMoney;

    private Integer state; // 0: pending, 1: approved , 2: rejected
}
