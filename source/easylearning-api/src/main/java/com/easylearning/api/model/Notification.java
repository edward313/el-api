package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_notification")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Notification extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @Column(name = "id_user")
    private Long idUser;
    @Column(name = "ref_id")
    private String refId;
    private Integer state;
    private Integer kind;
    @Column(columnDefinition = "text")
    private String msg;

}
