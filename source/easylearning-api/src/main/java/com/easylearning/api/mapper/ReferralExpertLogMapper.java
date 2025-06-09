package com.easylearning.api.mapper;


import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.expert.referralLog.ReferralExpertLogDto;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ReferralExpertLog;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ExpertMapper.class})
public interface ReferralExpertLogMapper {

    @Mapping(source = "id",target = "id")
    @Mapping(source = "expert",target = "expert", qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @Mapping(source = "refExpert",target = "refExpert", qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @Mapping(source = "usedTime",target = "usedTime")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToReferralExpertLogDto")
    ReferralExpertLogDto fromEntityToExpertDto(ReferralExpertLog referralExpertLog);

    @IterableMapping(elementTargetType = ReferralExpertLogDto.class,qualifiedByName = "fromEntityToReferralExpertLogDto")
    @BeanMapping(ignoreByDefault = true)
    List<ReferralExpertLogDto> fromEntityToReferralExpertLogDtoList(List<ReferralExpertLog> list);
}
