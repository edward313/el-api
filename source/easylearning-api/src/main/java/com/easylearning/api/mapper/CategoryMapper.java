package com.easylearning.api.mapper;

import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.dto.category.ElasticCategoryDto;
import com.easylearning.api.form.category.CreateCategoryForm;
import com.easylearning.api.form.category.UpdateCategoryForm;
import com.easylearning.api.model.Category;
import com.easylearning.api.model.elastic.ElasticCategory;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "kind", target = "kind")
    @Named("fromCreateCategory")
    @BeanMapping(ignoreByDefault = true)
    Category fromCreateCategory(CreateCategoryForm createCategoryForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Named("mappingForUpdateCategory")
    @BeanMapping(ignoreByDefault = true)
    void mappingForUpdateServiceCategory(UpdateCategoryForm updateServiceCategoryForm, @MappingTarget Category category);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @Named("fromEntityToCategoryDto")
    @BeanMapping(ignoreByDefault = true)
    CategoryDto fromEntityToCategoryDto(Category category);
    @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromEntityToCategoryDto")
    List<CategoryDto> fromEntityToDtoList(List<Category> categories);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCategoryToCompleteDto")
    CategoryDto fromCategoryToCompleteDto(Category category);

    @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromCategoryToCompleteDto")
    List<CategoryDto> fromCategoryToComplteDtoList(List<Category> categories);

    @Mapping(source = "id",target = "categoryId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "createdBy", target = "createdBy")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToElasticCategory")
    ElasticCategory fromEntityToElasticCategory(Category category);

    @IterableMapping(elementTargetType = ElasticCategory.class,qualifiedByName = "fromEntityToElasticCategory")
    @BeanMapping(ignoreByDefault = true)
    List<ElasticCategory> fromEntityToElasticCategoryList(List<Category> list);

    @Mapping(source = "categoryId",target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromElasticEntityToElasticCategoryDto")
    ElasticCategoryDto fromElasticEntityToCategoryDto(ElasticCategory elasticCategory);
}
