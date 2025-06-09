package com.easylearning.api.mapper;

import com.easylearning.api.dto.cartItem.CartItemDto;
import com.easylearning.api.model.CartItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class, CourseMapper.class})
public interface CartItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransactionClient")
    @Mapping(source = "timeCreated", target = "timeCreated")
    @Mapping(source = "sellCode", target = "sellCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCartItemDto")
    CartItemDto fromEntityToCartItemDto(CartItem cartItem);

    @IterableMapping(elementTargetType = CartItemDto.class, qualifiedByName = "fromEntityToCartItemDto")
    @BeanMapping(ignoreByDefault = true)
    List<CartItemDto> fromEntityToCartItemDtoList(List<CartItem> list);
}
