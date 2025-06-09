package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_revenue_share")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RevenueShare extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="course_transaction_id")
    private CourseTransaction courseTransaction;
    @ManyToOne
    @JoinColumn(name ="source_student_id")
    private Student sourceStudent;
    @ManyToOne
    @JoinColumn(name ="seller_id")
    private Student seller;
    @ManyToOne
    @JoinColumn(name ="source_seller_id")
    private Student sourceSeller;
    @ManyToOne
    @JoinColumn(name ="expert_id")
    private Expert expert;
    @ManyToOne
    @JoinColumn(name ="source_expert_id")
    private Expert sourceExpert;
    private Integer kind; // 0: ref, 1 system, 2 free course, 4 direct, 5 out
    @Column(name = "ref_kind")
    private Integer refKind; // 1 std, 2 seller, 3 expert
    @Column(name = "ratio_share")
    private Integer ratioShare;
    @Column(name = "revenue_money")
    private Double revenueMoney;
    @Column(name = "payout_status")
    private Integer payoutStatus;
}

