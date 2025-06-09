package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_booking")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Booking extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="student_id")
    private Student student;
    private Double totalMoney = 0.0;
    private Double saleOffMoney = 0.0;
    private Double couponMoney = 0.0;
    private Integer state;
    private Integer paymentMethod;
    private Integer payoutStatus;
    private String code;
    @ManyToOne
    @JoinColumn(name ="promotion_id")
    private Promotion promotion;
    @Column(columnDefinition = "LONGTEXT")
    private String paymentData;
    @Column(name = "coupon_sell_code")
    private String couponSellCode;
}
