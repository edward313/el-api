package com.easylearning.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.dto.expert.ElasticExpertDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.expert.ExpertInfoDto;
import com.easylearning.api.form.expert.CreateExpertForm;
import com.easylearning.api.form.expert.UpdateExpertForm;
import com.easylearning.api.form.expert.UpdateExpertProfileForm;
import com.easylearning.api.form.expertRegistraion.ApproveExpertRegistrationForm;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ExpertRegistration;
import com.easylearning.api.model.elastic.ElasticExpert;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class, NationMapper.class})
public interface ExpertMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpert")
    @Mapping(source = "referralCode", target = "myReferralCode")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "totalCourse", target = "totalCourse")
    @Mapping(source = "totalStudent", target = "totalStudent")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDtoForProfile")
    ExpertDto fromEntityToExpertDtoForProfile(Expert expert);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpert")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDto")
    ExpertDto fromEntityToExpertDto(Expert expert);

    @IterableMapping(elementTargetType = ExpertDto.class, qualifiedByName = "fromEntityToExpertDto")
    @BeanMapping(ignoreByDefault = true)
    List<ExpertDto> fromEntityToExpertDtoList(List<Expert> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpert")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDtoAutoComplete")
    ExpertDto fromEntityToExpertDtoAutoComplete(Expert expert);

    @IterableMapping(elementTargetType = ExpertDto.class, qualifiedByName = "fromEntityToExpertDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<ExpertDto> fromEntityToExpertDtoAutoCompleteList(List<Expert> list);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateExpertFormToEntity")
    Expert fromCreateExpertFormToEntity(CreateExpertForm createExpertForm);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "ward", target = "ward")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "province", target = "province")
    @Named("fromExpertRegistrationToExpert")
    @BeanMapping(ignoreByDefault = true)
    Expert fromExpertRegistrationToExpert(ExpertRegistration expertRegistration);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @Named("fromExpertRegistrationToExpert")
    @BeanMapping(ignoreByDefault = true)
    Expert fromCreateFormToExpert(CreateExpertForm createExpertForm);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "wardId", target = "wardId")
    @Mapping(source = "districtId", target = "districtId")
    @Mapping(source = "provinceId", target = "provinceId")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "password", target = "password")
    @Named("formApproveFormToCreateExpertForm")
    @BeanMapping(ignoreByDefault = true)
    CreateExpertForm formApproveFormToCreateExpertForm(ApproveExpertRegistrationForm approveExpertRegistrationForm);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "ordering", target = "ordering")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromUpdateExpertFormToEntity")
    void fromUpdateExpertFormToEntity(UpdateExpertForm updateExpertForm, @MappingTarget Expert expert);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "avatar", target = "account.avatarPath")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromUpdateExpertProfileFormToEntity")
    void fromUpdateExpertProfileFormToEntity(UpdateExpertProfileForm updateExpertProfileFormForm, @MappingTarget Expert expert);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDtoForCourseTransaction")
    ExpertDto fromEntityToExpertDtoForCourseTransaction(Expert expert);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpertClient")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDtoForCategoryHomeClient")
    ExpertDto fromEntityToExpertDtoForCategoryHomeClient(Expert expert);

    @Mapping(source = "id", target = "expertId")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToElasticExpert")
    ElasticExpert fromEntityToElasticExpert(Expert expert);

    @IterableMapping(elementTargetType = ElasticExpert.class, qualifiedByName = "fromEntityToElasticExpert")
    @BeanMapping(ignoreByDefault = true)
    List<ElasticExpert> fromEntityToElasticExpertList(List<Expert> list);

    @Mapping(source = "expertId", target = "id")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "isSystemExpert", target = "isSystemExpert")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromElasticEntityToElasticExpertDto")
    ElasticExpertDto fromElasticEntityToExpertDto(ElasticExpert elasticExpert);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.avatarPath", target = "avatar")
    @Mapping(source = "totalStudent", target = "totalStudent")
    @Mapping(source = "totalLessonTime", target = "totalLessonTime")
    @Mapping(source = "totalCourse", target = "totalCourse")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertInfoDto")
    ExpertInfoDto fromEntityToExpertInfoDto(Expert expert);

    @AfterMapping
    default void objectMapperForExpertInfo(Expert expert, @MappingTarget ExpertInfoDto expertInfoDto) {
        try {
            if (expert.getIdentification()!=null){
                JsonNode jsonNode = new ObjectMapper().readTree(expert.getIdentification());
                JsonNode introductionNode = jsonNode.get("introduction");
                if (introductionNode != null && !introductionNode.isNull()) {
                    expertInfoDto.setIntroduction(introductionNode.asText());
                }
                JsonNode coverNode = jsonNode.get("cover");
                if (coverNode != null && !coverNode.isNull()) {
                    expertInfoDto.setCover(coverNode.asText());
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Mapping(source = "id", target = "id")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpertClient")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalStudent", target = "totalStudent")
    @Mapping(source = "totalLessonTime", target = "totalLessonTime")
    @Mapping(source = "totalCourse", target = "totalCourse")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "isOutstanding", target = "isOutstanding")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertDtoForClient")
    ExpertDto fromEntityToExpertDtoForClient(Expert expert);

    @IterableMapping(elementTargetType = ExpertDto.class, qualifiedByName = "fromEntityToExpertDtoForClient")
    @BeanMapping(ignoreByDefault = true)
    List<ExpertDto> fromEntityToExpertDtoForClientList(List<Expert> list);
}
