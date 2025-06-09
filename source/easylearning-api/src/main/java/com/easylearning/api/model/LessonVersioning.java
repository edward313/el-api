package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_lesson_versioning")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class LessonVersioning extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private String name;
    private String thumbnail;
    private Boolean isPreview;
    private Integer kind;
    @Column(columnDefinition = "text")
    private String content;
    private String urlDocument;
    @Column(columnDefinition = "text")
    private String description;
    private Integer ordering;
    private Long videoDuration = 0L;
    @ManyToOne
    @JoinColumn(name ="course_id")
    private Course course;
    private Integer state;
    @ManyToOne
    @JoinColumn(name ="version_id")
    private Version version;
    private Long visualId;
}
