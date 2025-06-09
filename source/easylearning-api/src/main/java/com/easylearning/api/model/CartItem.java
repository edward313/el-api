package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_cart_item")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CartItem {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name ="course_id")
    private Course course;
    @Column(name = "time_created")
    private Date timeCreated;
    private String sellCode;
    @Column(name = "extra_money")
    private Double extraMoney = 0.0;
    @Column(name = "temp_sell_code")
    private String tempSellCode;
}
