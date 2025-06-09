package com.easylearning.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_el_course_review_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CourseReviewHistory extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @ManyToOne
    @JoinColumn(name ="version_id")
    private Version version;
    private Date date;
    private Integer state; // 0 init, 1 submit, 2 approve, 3 reject
    @Column(columnDefinition = "TEXT")
    private String reason;
}
