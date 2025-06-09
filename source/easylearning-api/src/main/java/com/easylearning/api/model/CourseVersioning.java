package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_course_versioning")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CourseVersioning extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private String name;
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;
    @Column(name = "description" ,  columnDefinition = "LONGTEXT")
    private String description;
    private String avatar;
    private String banner;
    private Double price;
    private Integer saleOff;
    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category field;
    @ManyToOne
    @JoinColumn(name ="expert_id")
    private Expert expert;
    @Column(name = "kind") // 1 single, 2 combo
    private Integer kind;
    @Column(name = "is_seller_course")
    private Boolean isSellerCourse = false;
    @Column(name = "total_review")
    private Integer totalReview = 0;
    @Column(name = "average_star")
    private Float averageStar = 0F;
    @Column(name = "total_study_time")
    private Long totalStudyTime = 0L;
    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0;
    @Column(name = "total_lesson")
    private Integer totalLesson = 0;
    @ManyToOne
    @JoinColumn(name ="version_id")
    private Version version;
    private Long visualId;
}
