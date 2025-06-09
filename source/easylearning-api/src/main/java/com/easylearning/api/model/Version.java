package com.easylearning.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_version")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Version extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private Date date;
    private Integer versionCode;
    private Integer state; // 0 init, 1 submit, 2 approve, 3 reject
    @Column(columnDefinition = "TEXT")
    private String reviewNote;
    private Long courseId;
}
