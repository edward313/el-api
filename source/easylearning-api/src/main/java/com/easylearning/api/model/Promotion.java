package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_promotion")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Promotion extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    @Column(name = "description",columnDefinition = "text")
    private String description;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    private Integer kind; // 1 money, 2 percent
    private Integer type; //1 use one, 2 use multiple

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "limit_value")
    private Double limitValue;

    private Integer quantity;

    private Integer state; // 0 created, 1 running, 2 end, 3 cancel

    @Column(name = "num_random")
    private Integer numRandom;

    private String prefix;
}
