package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_statistical")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Statistical extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @Column(name = "statisticalKey", unique =  true)
    private String statisticalKey;
    @Column(name = "statisticalValue")
    private String statisticalValue;
}
