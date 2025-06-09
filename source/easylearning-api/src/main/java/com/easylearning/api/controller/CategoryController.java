package com.easylearning.api.controller;


import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.form.category.CreateCategoryForm;
import com.easylearning.api.form.category.UpdateCategoryForm;
import com.easylearning.api.mapper.CategoryMapper;
import com.easylearning.api.model.Category;
import com.easylearning.api.model.criteria.CategoryCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryHomeRepository categoryHomeRepository;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    ExpertRepository expertRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RevenueShareRepository revenueShareRepository;


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<CategoryDto>> listCategory(@Valid CategoryCriteria categoryCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<CategoryDto>> apiMessageDto = new ApiMessageDto<>();

        Page<Category> serviceCategories = categoryRepository.findAll(CategoryCriteria.findCategoryByCriteria(categoryCriteria),pageable);
        ResponseListDto<CategoryDto> responseListDto = new ResponseListDto(categoryMapper.fromEntityToDtoList(serviceCategories.getContent()),serviceCategories.getTotalElements(), serviceCategories.getTotalPages());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get category list success");
        return  apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<CategoryDto> getCategory(@PathVariable("id") Long id) {
        ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();
        Category serviceCategory = categoryRepository.findById(id).orElse(null);
        if(serviceCategory == null){
            throw new NotFoundException("Category not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(categoryMapper.fromEntityToCategoryDto(serviceCategory));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get category success.");
        return  apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_D')")
    public ApiMessageDto<String> deleteCategory(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new BadRequestException("Category not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        categoryHomeRepository.deleteByCategory(id);
        reviewRepository.deleteAllByCategoryId(id);
        revenueShareRepository.deleteAllByCategoryId(id);
        courseTransactionRepository.deleteAllByCategoryId(id);
        courseRetailRepository.deleteAllByCategoryId(id);
        cartItemRepository.deleteAllCartItemByCategoryId(id);
        registrationRepository.deleteAllRegistrationByCategoryId(id);
        completionRepository.deleteAllCompletionByCategoryId(id);
        lessonRepository.deleteAllLessonByCategoryId(id);
        courseComboDetailRepository.deleteAllByCategoryId(id);
        newsRepository.deleteByCategoryId(id);
        expertRepository.updateExpertsAfterDeletingCoursesByCategoryId(id);
        courseRepository.deleteAllByFieldId(id);
        categoryRepository.deleteById(id);
        apiMessageDto.setMessage("Delete category success");
        return apiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_C')")
    public ApiMessageDto<String> createCategory(@Valid @RequestBody CreateCategoryForm createCategoryForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Category categoryCheckName = categoryRepository.findByName(createCategoryForm.getName());
        if (categoryCheckName != null) {
            throw new BadRequestException("Category is exist",ErrorCode.CATEGORY_ERROR_EXIST);
        }
        categoryRepository.save(categoryMapper.fromCreateCategory(createCategoryForm));
        apiMessageDto.setMessage("Create category success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_U')")
    public ApiMessageDto<String> updateCategory(@Valid @RequestBody UpdateCategoryForm updateCategoryForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Category categoryCheckName = categoryRepository.findByName(updateCategoryForm.getName());
        if (categoryCheckName != null && !Objects.equals(categoryCheckName.getId(),updateCategoryForm.getCategoryId())) {
            throw new BadRequestException("Update category fail",ErrorCode.CATEGORY_ERROR_EXIST);
        }

        Category category = categoryRepository.findById(updateCategoryForm.getCategoryId()).orElse(null);
        if (category == null) {
            throw new NotFoundException("Category not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }

        categoryMapper.mappingForUpdateServiceCategory(updateCategoryForm, category);
        categoryRepository.save(category);
        apiMessageDto.setMessage("Update category success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> autoCompleteCategoryAuto(@Valid CategoryCriteria categoryCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<CategoryDto>> responseListDto = new ResponseListDto<>();
        Page<Category> categories = categoryRepository.findAll(CategoryCriteria.findCategoryByCriteria(categoryCriteria), pageable);
        List<CategoryDto> addressAdminDtos = categoryMapper.fromCategoryToComplteDtoList(categories.getContent());

        responseListDto.setContent(addressAdminDtos);
        responseListDto.setTotalPages(categories.getTotalPages());
        responseListDto.setTotalElements(categories.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get category success");
        return apiMessageDto;
    }
}
