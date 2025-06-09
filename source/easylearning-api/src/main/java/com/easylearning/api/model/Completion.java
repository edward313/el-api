package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_completion")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Completion {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name ="student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name ="course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name ="lesson_id")
    private Lesson lesson;

    @Column(name = "date_finished")
    private Date dateFinished;
    private Boolean isFinished = false;
    private Long secondProgress;
}
