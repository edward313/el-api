package com.easylearning.api.mapper;

import com.easylearning.api.dto.version.VersionDto;
import com.easylearning.api.model.Version;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class})
public interface VersionMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "courseId", target = "courseId")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "versionCode", target = "versionCode")
    @Mapping(source = "reviewNote", target = "reviewNote")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToAdminDto")
    VersionDto fromEntityToAdminDto(Version version);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "reviewNote",target = "reviewNote")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToVersionDtoForMyCourse")
    VersionDto fromEntityToVersionDtoForMyCourse(Version version);

    @IterableMapping(elementTargetType = VersionDto.class, qualifiedByName = "fromEntityToAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<VersionDto> fromEntityToAdminDtoList(List<Version> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "versionCode", target = "versionCode")
    @Mapping(source = "reviewNote", target = "reviewNote")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToDtoForVisual")
    VersionDto fromEntityToDtoForVisual(Version version);

}
