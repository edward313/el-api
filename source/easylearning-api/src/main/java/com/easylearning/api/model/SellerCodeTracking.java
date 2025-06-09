package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_seller_code_tracking")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SellerCodeTracking extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="student_id")
    private Student student;
    @Column(name="sell_code")
    private String sellCode ;
    @Column(unique = true, name="browser_code")
    private String browserCode ;
}
