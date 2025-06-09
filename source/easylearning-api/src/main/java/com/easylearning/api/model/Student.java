package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_student")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Student extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private Date birthday;
    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;
    private String address;
    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Nation ward;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private Nation district;
    @ManyToOne
    @JoinColumn(name = "province_id")
    private Nation province;
    private Boolean isSeller;
    @Column(unique = true)
    private String referralCode;
    @Column(name = "is_system_seller")
    private Boolean isSystemSeller = false;
    private String bankInfo;
    @Column(name = "identification", columnDefinition = "LONGTEXT")
    private String identification;
    @Column(name = "is_referral_bonus_paid")
    private Boolean isReferralBonusPaid = false;
}
