package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.categoryHome.CategoryHomeAdminDto;
import com.easylearning.api.dto.categoryHome.CategoryHomeClientDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.form.categoryHome.UpdateCategoryHomeForm;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.CategoryHomeMapper;
import com.easylearning.api.mapper.CourseMapper;
import com.easylearning.api.model.Category;
import com.easylearning.api.model.CategoryHome;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.criteria.CategoryCriteria;
import com.easylearning.api.model.criteria.CategoryHomeCriteria;
import com.easylearning.api.repository.CategoryHomeRepository;
import com.easylearning.api.repository.CategoryRepository;
import com.easylearning.api.repository.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/category-home")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryHomeController extends ABasicController {

    @Autowired
    private CategoryHomeRepository categoryHomeRepository;

    @Autowired
    private CategoryHomeMapper categoryHomeMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CH_L')")
    public ApiMessageDto<ResponseListDto<CategoryHomeAdminDto>> listCategory(@Valid CategoryHomeCriteria categoryHomeCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<CategoryHomeAdminDto>> apiMessageDto = new ApiMessageDto<>();
        Page<CategoryHome> categoryHomes = categoryHomeRepository.findAll(categoryHomeCriteria.getSpecification(),pageable);
        ResponseListDto<CategoryHomeAdminDto> responseListDto = new ResponseListDto(categoryHomeMapper.fromEntityToCategoryHomeAdminDtoList(categoryHomes.getContent()),categoryHomes.getTotalElements(), categoryHomes.getTotalPages());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get category list success");
        return  apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CH_V')")
    public ApiMessageDto<CategoryHomeAdminDto> getCategory(@PathVariable("id") Long id) {
        ApiMessageDto<CategoryHomeAdminDto> apiMessageDto = new ApiMessageDto<>();
        CategoryHome categoryHome = categoryHomeRepository.findById(id).orElse(null);
        if(categoryHome == null){
            throw new NotFoundException("Category Home not found",ErrorCode.CATEGORY_HOME_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(categoryHomeMapper.fromEntityToCategoryHomeAdminDto(categoryHome));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get category success.");
        return  apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CH_D')")
    public ApiMessageDto<String> deleteCategory(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CategoryHome categoryHome = categoryHomeRepository.findById(id).orElse(null);
        if(categoryHome == null){
            throw new NotFoundException("Category Home not found",ErrorCode.CATEGORY_HOME_ERROR_NOT_FOUND);
        }
        categoryHomeRepository.deleteById(id);
        apiMessageDto.setMessage("Delete category home success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CH_U')")
    public ApiMessageDto<String> updateCategory(@Valid @RequestBody UpdateCategoryHomeForm updateCategoryHomeForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CategoryHome categoryHome = categoryHomeRepository.findById(updateCategoryHomeForm.getId()).orElse(null);
        if(categoryHome == null){
            throw new NotFoundException("Category Home not found",ErrorCode.CATEGORY_HOME_ERROR_NOT_FOUND);
        }
        CategoryHome existCategoryHome = categoryHomeRepository.findFirstByCourseId(updateCategoryHomeForm.getCourseId()).orElse(null);
        if (existCategoryHome != null) {
            throw new BadRequestException("Category home is exist",ErrorCode.CATEGORY_HOME_ERROR_EXIST);
        }
        Course course = courseRepository.findByIdAndStatus(updateCategoryHomeForm.getCourseId(),LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if(course == null){
            throw new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        categoryHome.setCourse(course);
        categoryHomeRepository.save(categoryHome);
        apiMessageDto.setMessage("Update category home success");
        return apiMessageDto;
    }

    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CategoryHomeClientDto>>> clientList(@Valid CategoryCriteria categoryCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CategoryHomeClientDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<CategoryHomeClientDto>> responseListDto = new ResponseListDto<>();
        categoryCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        categoryCriteria.setIgnoreKind(LifeUniConstant.CATEGORY_KIND_NEWS);
        Page<Category> categories = categoryRepository.findAll(CategoryCriteria.findCategoryByCriteria(categoryCriteria), pageable);
        List<CategoryHomeClientDto> categoryHomeClientDtos = categoryHomeMapper.fromCategoryToCategoryHomeClientDtoList(categories.getContent());
        for (CategoryHomeClientDto categoryHomeClientDto : categoryHomeClientDtos) {
            List<CourseDto> courseDtoList;
            // if top free & top course sort by soldQuantity
            if (Objects.equals(categoryHomeClientDto.getCategory().getKind(), LifeUniConstant.CATEGORY_KIND_TOP_FREE) || Objects.equals(categoryHomeClientDto.getCategory().getKind(), LifeUniConstant.CATEGORY_KIND_TOP_CHARGE)) {
                courseDtoList = courseMapper.fromEntityToCourseDtoForCategoryHomeClientList(categoryHomeRepository.findAllCourseByCategoryIdAndStatusAndCourseStatus(categoryHomeClientDto.getCategory().getId(),
                        LifeUniConstant.STATUS_ACTIVE, LifeUniConstant.STATUS_ACTIVE));
            } else { // sort by createDate of home category
                courseDtoList = courseMapper.fromEntityToCourseDtoForCategoryHomeClientList(categoryHomeRepository.findAllNewCourseByCategoryIdAndStatusAndCourseStatus(categoryHomeClientDto.getCategory().getId(),
                        LifeUniConstant.STATUS_ACTIVE, LifeUniConstant.STATUS_ACTIVE));
            }
            categoryHomeClientDto.setCourses(courseDtoList);
        }

        responseListDto.setContent(categoryHomeClientDtos);
        responseListDto.setTotalPages(categories.getTotalPages());
        responseListDto.setTotalElements(categories.getTotalElements());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get category success");
        return apiMessageDto;
    }

}
