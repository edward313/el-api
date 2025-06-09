package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_promotion_code")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PromotionCode{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private Integer status;

    @Column(unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    private Integer quantityUsed;
}
