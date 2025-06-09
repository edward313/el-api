package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_course_transaction")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CourseTransaction extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="booking_id")
    private Booking booking;
    @ManyToOne
    @JoinColumn(name ="expert_id")
    private Expert expert;
    @ManyToOne
    @JoinColumn(name ="seller_id")
    private Student seller;
    @ManyToOne
    @JoinColumn(name ="course_id")
    private Course course;
    private Double price;
    private Double originalPrice;
    @Column(name="ref_sell_code")
    private String refSellCode ;
}
