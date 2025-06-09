package com.easylearning.api.mapper;


import com.easylearning.api.dto.categoryHome.CategoryHomeAdminDto;
import com.easylearning.api.dto.categoryHome.CategoryHomeClientDto;
import com.easylearning.api.dto.categoryHome.CategoryHomeDto;
import com.easylearning.api.model.Category;
import com.easylearning.api.model.CategoryHome;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={CategoryMapper.class, CourseMapper.class})
public interface CategoryHomeMapper {

    @Mapping(source = "id",target = "id")
    @Mapping(source = "category", target = "category",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "course", target = "course",qualifiedByName = "fromEntityToCourseDto")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCategoryHomeAdminDto")
    CategoryHomeAdminDto fromEntityToCategoryHomeAdminDto(CategoryHome categoryHome);

    @IterableMapping(elementTargetType = CategoryHomeAdminDto.class,qualifiedByName = "fromEntityToCategoryHomeAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<CategoryHomeAdminDto> fromEntityToCategoryHomeAdminDtoList(List<CategoryHome> list);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "category", target = "category",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "course", target = "course",qualifiedByName = "fromEntityToCourseDto")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCategoryHomeDtoClient")
    CategoryHomeDto fromEntityToCategoryHomeDtoClient(CategoryHome categoryHome);

    @IterableMapping(elementTargetType = CategoryHomeDto.class,qualifiedByName = "fromEntityToCategoryHomeDtoClient")
    @BeanMapping(ignoreByDefault = true)
    List<CategoryHomeDto> fromEntityToCategoryHomeDtoClientList(List<CategoryHome> list);

    @Mapping(source = "id", target = "category.id")
    @Mapping(source = "name", target = "category.name")
    @Mapping(source = "description", target = "category.description")
    @Mapping(source = "image", target = "category.image")
    @Mapping(source = "kind", target = "category.kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCategoryToCategoryHomeClientDto")
    CategoryHomeClientDto fromCategoryToCategoryHomeClientDto(Category category);

    @IterableMapping(elementTargetType = CategoryHomeClientDto.class, qualifiedByName = "fromCategoryToCategoryHomeClientDto")
    List<CategoryHomeClientDto> fromCategoryToCategoryHomeClientDtoList(List<Category> categories);
}
