package com.easylearning.api.mapper;

import com.easylearning.api.dto.account.AccountAutoCompleteDto;
import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.form.GoogleUserInfo;
import com.easylearning.api.dto.facebook.FacebookUserInfo;
import com.easylearning.api.form.expert.CreateExpertForm;
import com.easylearning.api.form.expert.UpdateExpertForm;
import com.easylearning.api.form.expertRegistraion.ApproveExpertRegistrationForm;
import com.easylearning.api.form.student.*;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.ExpertRegistration;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {GroupMapper.class})
public interface AccountMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "group", target = "group", qualifiedByName = "fromEntityToGroupDto")
    @Mapping(source = "lastLogin", target = "lastLogin")
    @Mapping(source = "avatarPath", target = "avatar")
    @Mapping(source = "isSuperAdmin", target = "isSuperAdmin")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDto")
    AccountDto fromAccountToDto(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "fullName", target = "fullName")
    @Named("fromAccountToAutoCompleteDto")
    AccountAutoCompleteDto fromAccountToAutoCompleteDto(Account account);


    @IterableMapping(elementTargetType = AccountAutoCompleteDto.class)
    List<AccountAutoCompleteDto> convertAccountToAutoCompleteDto(List<Account> list);


    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    Account fromSignUpStudentToAccount(SignUpStudentForm signUpStudentForm);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "fullName")
    @Mapping(source = "picture", target = "avatarPath")
    @Mapping(source = "id", target = "googleId")
    @BeanMapping(ignoreByDefault = true)
    Account fromGoogleUserInfoToAccount(GoogleUserInfo googleUserInfo);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "avatar", target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    Account fromExpertRegistrationToAccount(ExpertRegistration expertRegistration);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "fullName", target = "fullName")
    @BeanMapping(ignoreByDefault = true)
    Account fromCreateExpertFormToAccount(CreateExpertForm createExpertForm);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "fullName")
    @Mapping(source = "id", target = "facebookId")
    @BeanMapping(ignoreByDefault = true)
    Account fromFacebookUserInfoToAccount(FacebookUserInfo facebookUserInfo);


    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @Mapping(source = "status",target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateExpertFormToEntity(UpdateExpertForm updateExpertForm, @MappingTarget Account account );

    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @Mapping(source = "status",target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateStudentFormToEntity(UpdateStudentForm updateStudentForm, @MappingTarget Account account );

    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @Mapping(source = "status",target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateSellerFormToEntity(UpdateSellerForm updateSellerForm, @MappingTarget Account account );

    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateStudentProfileFormToEntity(UpdateStudentProfileForm updateStudentProfileForm, @MappingTarget Account account );

    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateSellerProfileFormToEntity(UpdateSellerProfileForm updateSellerProfileForm, @MappingTarget Account account );


    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "kind",target = "kind")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatar")
    @Mapping(source = "group", target = "group", qualifiedByName = "fromEntityToGroupDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDtoForExpert")
    AccountDto fromAccountToDtoForExpert(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatar")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDtoForExpertClient")
    AccountDto fromAccountToDtoForExpertClient(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "kind",target = "kind")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatar")
    @Mapping(source = "group", target = "group", qualifiedByName = "fromEntityToGroupDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDtoForStudent")
    AccountDto fromAccountToDtoForStudent(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "kind",target = "kind")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatar")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDtoForReview")
    AccountDto fromAccountToDtoForReview(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "kind",target = "kind")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatar")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDtoForPeriodDetail")
    AccountDto fromAccountToDtoForPeriodDetail(Account account);

}
