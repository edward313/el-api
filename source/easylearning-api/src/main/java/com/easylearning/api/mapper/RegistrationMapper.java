package com.easylearning.api.mapper;

import com.easylearning.api.dto.completion.CompletionDto;
import com.easylearning.api.dto.registration.RegistrationDto;
import com.easylearning.api.model.Completion;
import com.easylearning.api.model.Registration;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={StudentMapper.class, CourseMapper.class})
public interface RegistrationMapper {

    @Mapping(source = "id",target = "id")
    @Mapping(source = "isFinished",target = "isFinished")
    @Mapping(source = "student", target = "student",qualifiedByName = "fromEntityToStudentAdminDto")
    @Mapping(source = "course", target = "course",qualifiedByName = "fromEntityToCourseDto")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToRegistrationDto")
    RegistrationDto fromEntityToRegistrationDto(Registration registration);

    @IterableMapping(elementTargetType = RegistrationDto.class,qualifiedByName = "fromEntityToRegistrationDto")
    @BeanMapping(ignoreByDefault = true)
    List<RegistrationDto> fromEntityToRegistrationDtoList(List<Registration> list);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "isFinished",target = "isFinished")
    @Mapping(source = "student", target = "student",qualifiedByName = "fromStudentToDtoForRegistration")
    @Mapping(source = "course", target = "course",qualifiedByName = "fromEntityToCourseDtoForRegistration")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMyStudentRegistrationDto")
    RegistrationDto fromEntityToMyStudentRegistrationDto(Registration registration);

    @IterableMapping(elementTargetType = RegistrationDto.class,qualifiedByName = "fromEntityToMyStudentRegistrationDto")
    @BeanMapping(ignoreByDefault = true)
    List<RegistrationDto> fromEntityToMyStudentRegistrationDtoList(List<Registration> list);
}
