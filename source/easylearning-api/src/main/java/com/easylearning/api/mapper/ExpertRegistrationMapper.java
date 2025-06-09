package com.easylearning.api.mapper;

import com.easylearning.api.dto.expertRegistration.ExpertRegistrationtAdminDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import com.easylearning.api.form.account.RegisterProfileFacebookForm;
import com.easylearning.api.form.account.RegisterProfileGoogleForm;
import com.easylearning.api.form.expertRegistraion.CreateExpertRegistrationForm;
import com.easylearning.api.form.student.UpdateStudentForm;
import com.easylearning.api.form.student.UpdateStudentProfileForm;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ExpertRegistration;
import com.easylearning.api.model.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, NationMapper.class})
public interface ExpertRegistrationMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "introduction", target = "introduction")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertRegistrationAdminDto")
    ExpertRegistrationtAdminDto fromEntityToExpertRegistrationAdminDto(ExpertRegistration expertRegistration);

    @IterableMapping(elementTargetType = ExpertRegistrationtAdminDto.class, qualifiedByName = "fromEntityToExpertRegistrationAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<ExpertRegistrationtAdminDto> fromEntityToExpertRegistrationAdminDtoList(List<ExpertRegistration> list);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "introduction", target = "introduction")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "birthday", target = "birthday")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToExpertRegistrationAdminDto")
    ExpertRegistration fromCreateFormToExpertRegistration(CreateExpertRegistrationForm createExpertRegistrationForm);

}
