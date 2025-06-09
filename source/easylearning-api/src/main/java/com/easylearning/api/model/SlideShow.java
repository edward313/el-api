package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_slideshow")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SlideShow extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private String image;

    @Column(name = "mobile_image")
    private String mobileImage;

    private String url;

    private Integer action;

    private Integer ordering;
}
