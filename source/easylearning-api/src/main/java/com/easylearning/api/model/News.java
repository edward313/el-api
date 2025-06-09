package com.easylearning.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_el_news")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class News extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.easylearning.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    private Integer kind; // 1 introduction

    @Column(name = "content",columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "title")
    private String title;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "mobile_banner")
    private String mobileBanner;

    @Column(name = "banner")
    private String banner;

    @Column(name = "avatar")
    private String avatar;
}

