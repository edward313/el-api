package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_expert")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Expert extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    private Date birthday;

    @Column(unique = true, name="referral_code")
    private String referralCode ;

    @Column(name = "is_system_expert")
    private Boolean isSystemExpert = false;
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
    @Column(name = "bank_info", columnDefinition = "text")
    private String bankInfo;
    @Column(name = "identification", columnDefinition = "LONGTEXT")
    private String identification;
    @Column(name = "total_course")
    private Integer totalCourse = 0;
    @Column(name = "total_lesson_time")
    private Long totalLessonTime = 0L;
    @Column(name = "total_student")
    private Integer totalStudent = 0;
    private Integer ordering;
    private Boolean isOutstanding = false;
}
