package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_registration")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Registration extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name ="course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name ="student_id")
    private Student student;

    private Boolean isFinished;
}
