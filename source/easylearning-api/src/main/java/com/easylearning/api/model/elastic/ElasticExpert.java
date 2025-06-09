package com.easylearning.api.model.elastic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;

@Document(indexName = "elastic_expert")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ElasticExpert {
    @Id
    @Field(type = FieldType.Text, index = false)
    private String id;

    @Field(type = FieldType.Long, index = false)
    private Long expertId;

    @Field(type = FieldType.Text, index = false)
    private String fullName;

    @Field(type = FieldType.Long,index = false)
    private Date birthday;

    @Field(type = FieldType.Keyword,index = false)
    private String referralCode;

    @Field(type = FieldType.Boolean,index = false)
    private Boolean isSystemExpert;

    @Field(type = FieldType.Integer,index = false)
    private Integer status;

    @Field(type = FieldType.Long,index = false)
    private Date createdDate;

    @Field(type = FieldType.Long,index = false)
    private Date modifiedDate;

    @Field(type = FieldType.Text,index = false)
    private String createdBy;

    @Field(type = FieldType.Text,index = false)
    private String modifiedBy;
}
