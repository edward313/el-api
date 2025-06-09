package com.easylearning.api.mapper;

import com.easylearning.api.dto.referralSellerLog.ReferralSellerLogDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import com.easylearning.api.form.student.UpdateStudentForm;
import com.easylearning.api.model.ReferralSellerLog;
import com.easylearning.api.model.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class})
public interface ReferralSellerLogMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "usedTime", target = "usedTime")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForReferralSellerLog")
    @Mapping(source = "refStudent", target = "seller", qualifiedByName = "fromStudentToDtoForReferralSellerLog")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToDto")
    ReferralSellerLogDto fromEntityToDto(ReferralSellerLog referralSellerLog);

    @IterableMapping(elementTargetType = ReferralSellerLogDto.class, qualifiedByName = "fromEntityToDto")
    @BeanMapping(ignoreByDefault = true)
    List<ReferralSellerLogDto> fromEntityListToDtoList(List<ReferralSellerLog> list);
}
