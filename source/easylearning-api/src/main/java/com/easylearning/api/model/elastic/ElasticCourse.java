package com.easylearning.api.model.elastic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;


@Document(indexName = "#{@environment.getProperty('elastic.index.course')}")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Setting(settingPath = "/elastic-settings/course-name.json")
public class ElasticCourse {
    @Id
    @Field(type = FieldType.Text,index = false)
    private String id;

    @Field(type = FieldType.Long)
    private Long courseId;

    @Field(type = FieldType.Text, analyzer = "custom_analyzer")
    private String name;

    @Field(type = FieldType.Text, index = false)
    private String shortDescription;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text,index = false)
    private String avatar;

    @Field(type = FieldType.Text,index = false)
    private String banner;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword,index = false)
    private Integer saleOff;

    @Field(type = FieldType.Object)
    private ElasticCategory field;

    @Field(type = FieldType.Object)
    private ElasticExpert expert;

    @Field(type = FieldType.Integer,index = false)
    private Integer kind;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Long)
    private Date createdDate;

    @Field(type = FieldType.Long,index = false)
    private Date modifiedDate;

    @Field(type = FieldType.Text,index = false)
    private String createdBy;

    @Field(type = FieldType.Text,index = false)
    private String modifiedBy;

    @Field(type = FieldType.Boolean,index = false)
    private Boolean isSellerCourse;

    @Field(type = FieldType.Integer)
    private Integer totalReview = 0;

    @Field(type = FieldType.Float,index = false)
    private Float averageStar = 0F;

    @Field(type = FieldType.Long,index = false)
    private Long totalStudyTime = 0L;

    @Field(type = FieldType.Integer,index = false)
    private Integer soldQuantity = 0;

    @Field(type = FieldType.Integer,index = false)
    private Integer totalLesson = 0;
}
