package com.easylearning.api.mapper;

import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import com.easylearning.api.form.account.RegisterProfileFacebookForm;
import com.easylearning.api.form.account.RegisterProfileGoogleForm;
import com.easylearning.api.form.student.*;
import com.easylearning.api.model.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class, NationMapper.class})
public interface StudentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForStudent")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToStudentAdminDto")
    StudentAdminDto fromEntityToStudentAdminDto(Student student);


    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateFormToStudentEntity")
    void updateFromUpdateFormToStudentEntity(UpdateStudentForm updateStudentForm,@MappingTarget Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateSellerFormToStudentEntity")
    void updateFromUpdateSellerFormToStudentEntity(UpdateSellerForm updateSellerForm, @MappingTarget Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromRegisterProfileGoogleFormToStudentEntity")
    void updateFromRegisterProfileGoogleFormToStudentEntity(RegisterProfileGoogleForm registerProfileGoogleForm, @MappingTarget Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateStudentProfileFormToStudentEntity")
    void updateFromUpdateStudentProfileFormToStudentEntity(UpdateStudentProfileForm updateStudentProfileForm, @MappingTarget Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateSellerProfileFormToStudentEntity")
    void updateFromUpdateSellerProfileFormToStudentEntity(UpdateSellerProfileForm updateSellerProfileForm, @MappingTarget Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromRegisterProfileGoogleFormToStudentEntity")
    void updateFromRegisterProfileFacebookFormToStudentEntity(RegisterProfileFacebookForm registerProfileGoogleForm, @MappingTarget Student student);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoAutoComplete")
    StudentDto fromStudentToDtoAutoComplete(Student student);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "myReferralCode")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForExpert")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoForProfile")
    StudentDto fromStudentToDtoForProfile(Student student);

    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromSignUpFormToEntity")
    Student fromSignUpFormToEntity(SignUpStudentForm signUpStudentForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoForTransaction")
    StudentDto fromStudentToDtoForTransaction(Student student);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoForReview")
    StudentDto fromStudentToDtoForReview(Student student);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoForRegistration")
    StudentAdminDto fromStudentToDtoForRegistration(Student student);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "isSeller", target = "isSeller")
    @Mapping(source = "referralCode", target = "referralCode")
    @Mapping(source = "isSystemSeller", target ="isSystemSeller")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForReview")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "identification", target = "identification")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromStudentToDtoForReferralSellerLog")
    StudentDto fromStudentToDtoForReferralSellerLog(Student student);

    @IterableMapping(elementTargetType = StudentAdminDto.class, qualifiedByName = "fromEntityToStudentAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<StudentAdminDto> fromStudentListToStudentAdminDtoList(List<Student> list);

    @IterableMapping(elementTargetType = StudentDto.class, qualifiedByName = "fromStudentToDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<StudentDto> fromStudentListToStudentDtoListAutocomplete(List<Student> list);
}
