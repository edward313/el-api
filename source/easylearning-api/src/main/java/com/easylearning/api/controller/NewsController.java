package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.lesson.LessonAdminDto;
import com.easylearning.api.dto.news.NewsDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.lesson.CreateLessonForm;
import com.easylearning.api.form.lesson.UpdateLessonForm;
import com.easylearning.api.form.news.CreateNewsForm;
import com.easylearning.api.form.news.UpdateNewsForm;
import com.easylearning.api.mapper.NewsMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.LessonCriteria;
import com.easylearning.api.model.criteria.NewsCriteria;
import com.easylearning.api.repository.CategoryRepository;
import com.easylearning.api.repository.NewsRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/news")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Log4j2
public class NewsController extends ABasicController{

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_L')")
    public ApiMessageDto<ResponseListDto<List<NewsDto>>> list(NewsCriteria newsCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<NewsDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<News> newsPage = newsRepository.findAll(newsCriteria.getSpecification(), pageable);
        ResponseListDto<List<NewsDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(newsMapper.fromEntityToNewsDtoList(newsPage.getContent()));
        responseListObj.setTotalPages(newsPage.getTotalPages());
        responseListObj.setTotalElements(newsPage.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get news list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<NewsDto>>> clientList(NewsCriteria newsCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<NewsDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<News> newsPage = newsRepository.findAll(newsCriteria.getSpecification(), pageable);
        ResponseListDto<List<NewsDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(newsMapper.fromEntityToNewsDtoListForClient(newsPage.getContent()));
        responseListObj.setTotalPages(newsPage.getTotalPages());
        responseListObj.setTotalElements(newsPage.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get news list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<NewsDto>>> autoComplete(NewsCriteria newsCriteria) {
        ApiMessageDto<ResponseListDto<List<NewsDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        newsCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Pageable pageable = PageRequest.of(0,10);
        Page<News> listCourse = newsRepository.findAll(newsCriteria.getSpecification(), pageable);
        ResponseListDto<List<NewsDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(newsMapper.fromEntityToNewsDtoAutoCompleteList(listCourse.getContent()));
        responseListObj.setTotalPages(listCourse.getTotalPages());
        responseListObj.setTotalElements(listCourse.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get auto-complete list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_V')")
    public ApiMessageDto<NewsDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<NewsDto> apiMessageDto = new ApiMessageDto<>();
        News news = newsRepository.findById(id).orElse(null);
        if (news == null) {
            throw new NotFoundException("News is not found", ErrorCode.NEWS_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(newsMapper.fromEntityToNewsDto(news));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
    @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<NewsDto> clientGet(@PathVariable("id") Long id) {
        ApiMessageDto<NewsDto> apiMessageDto = new ApiMessageDto<>();
        News news = newsRepository.findById(id).orElse(null);
        if (news == null) {
            throw new NotFoundException("News is not found", ErrorCode.NEWS_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(newsMapper.fromEntityToNewsDtoForClient(news));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateNewsForm createNewsForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Category category = categoryRepository.findById(createNewsForm.getCategoryId()).orElse(null);
        if (category == null) {
            throw new NotFoundException("Category is not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        checkCategoryKind(category,LifeUniConstant.CATEGORY_KIND_NEWS);
        News existingNews = newsRepository.findByTitle(createNewsForm.getTitle());
        if (existingNews != null) {
            throw new BadRequestException("News is exist",ErrorCode.NEWS_ERROR_EXISTED);
        }
        News news = newsMapper.fromCreateNewsFormToEntity(createNewsForm);
        news.setCategory(category);
        newsRepository.save(news);

        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Create news success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateNewsForm updateNewsForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        News news = newsRepository.findById(updateNewsForm.getId()).orElse(null);
        if (news == null) {
            throw new NotFoundException("News is not found", ErrorCode.NEWS_ERROR_NOT_FOUND);
        }
        if (!news.getTitle().equals(updateNewsForm.getTitle())) {
            News existingNewsTitle = newsRepository.findByTitle(updateNewsForm.getTitle());
            if (existingNewsTitle != null && !existingNewsTitle.getId().equals(news.getId())) {
                throw new BadRequestException("News title already exist",ErrorCode.NEWS_ERROR_EXISTED);
            }
        }
        newsMapper.updateNewsFromForm(updateNewsForm,news);

        if(!Objects.equals(news.getCategory().getId(), updateNewsForm.getCategoryId())){
            Category category = categoryRepository.findById(updateNewsForm.getCategoryId()).orElse(null);
            if (category == null) {
                throw new BadRequestException(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            }
            checkCategoryKind(category,LifeUniConstant.CATEGORY_KIND_NEWS);
            news.setCategory(category);
        }

        newsRepository.save(news);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Update news success");
        return apiMessageDto;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        News existingNews = newsRepository.findById(id).orElse(null);
        if (existingNews == null) {
            throw new NotFoundException("News is not found", ErrorCode.NEWS_ERROR_NOT_FOUND);
        }
        newsRepository.delete(existingNews);
        apiMessageDto.setMessage("Delete news success");
        return apiMessageDto;
    }
}
