package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_expert_registration")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ExpertRegistration extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category field;
    private String phone;
    private String email;
    private String avatar;
    private String address;
    @Column(columnDefinition = "text")
    private String introduction;
    private String referralCode;
    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Nation ward;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private Nation district;
    @ManyToOne
    @JoinColumn(name = "province_id")
    private Nation province;
    private String fullName;
    private Date birthday;
}
