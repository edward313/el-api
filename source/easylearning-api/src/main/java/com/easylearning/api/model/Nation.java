package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_nation")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Nation extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private String name;
    private String postCode;
    private Integer kind;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Nation parent;

}
